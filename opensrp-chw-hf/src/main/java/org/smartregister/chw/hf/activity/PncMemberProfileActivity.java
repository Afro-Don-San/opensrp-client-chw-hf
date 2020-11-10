package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CorePncMemberProfileActivity;
import org.smartregister.chw.core.activity.CorePncRegisterActivity;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.interactor.CorePncMemberProfileInteractor;
import org.smartregister.chw.core.model.ChildModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.ReferralCardViewAdapter;
import org.smartregister.chw.hf.contract.PncMemberProfileContract;
import org.smartregister.chw.hf.interactor.PncMemberProfileInteractor;
import org.smartregister.chw.hf.model.FamilyProfileModel;
import org.smartregister.chw.hf.presenter.PncMemberProfilePresenter;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Task;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;

public class PncMemberProfileActivity extends CorePncMemberProfileActivity implements PncMemberProfileContract.View {

    private CommonPersonObjectClient commonPersonObjectClient;
    private PncMemberProfilePresenter pncMemberProfilePresenter;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, PncMemberProfileActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, baseEntityID);
        passToolbarTitle(activity, intent);
        activity.startActivity(intent);
    }

    @Override
    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(this, memberObject.getBaseEntityId());
    }

    @Override
    protected void startFpRegister() {
        FpRegisterActivity.startFpRegistrationActivity(this, memberObject.getBaseEntityId(), memberObject.getDob(), CoreConstants.JSON_FORM.getFpRegistrationForm("female"), FamilyPlanningConstants.ActivityPayload.REGISTRATION_PAYLOAD_TYPE);
    }

    @Override
    protected void startFpChangeMethod() {
        FpRegisterActivity.startFpRegistrationActivity(this, memberObject.getBaseEntityId(), memberObject.getDob(), CoreConstants.JSON_FORM.getFpChangeMethodForm("female"), FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    protected void startMalariaFollowUpVisit() {
        // TODO -> Implement for HF
    }

    @Override
    protected void startHfMalariaFollowupForm() {
        MalariaFollowUpVisitActivityHelper.startMalariaFollowUpActivity(this, memberObject.getBaseEntityId());
    }

    @Override
    protected void startHivRegister() {
        try {
            HivRegisterActivity.startHIVFormActivity(this, memberObject.getBaseEntityId(),CoreConstants.JSON_FORM.getHivRegistration(),(new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getHivRegistration()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void startTbRegister() {
        try {
            TbRegisterActivity.startTbFormActivity(this, memberObject.getBaseEntityId(),CoreConstants.JSON_FORM.getTbRegistration(),(new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getTbRegistration()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getRemoveBabyMenuItem(MenuItem menuItem) {
        // TODO -> Implement for HF
    }

    @Override
    public void openMedicalHistory() {
        PncMedicalHistoryActivity.startMe(this, memberObject);
    }

    public void setReferralTasks(Set<Task> taskList) {
        if (notificationAndReferralRecyclerView != null && taskList.size() > 0) {
            RecyclerView.Adapter mAdapter = new ReferralCardViewAdapter(taskList, this, getCommonPersonObjectClient(), CoreConstants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY);
            notificationAndReferralRecyclerView.setAdapter(mAdapter);
            notificationAndReferralLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        findViewById(R.id.record_visit_panel).setVisibility(View.GONE);
        setCommonPersonObjectClient(getClientDetailsByBaseEntityID(baseEntityID));
    }

    @Override
    public void registerPresenter() {
        presenter = new PncMemberProfilePresenter(this, new PncMemberProfileInteractor(), memberObject);
    }

    @Override
    public void initializeFloatingMenu() {
        super.initializeFloatingMenu();
        if (baseAncFloatingMenu != null) {
            FloatingActionButton floatingActionButton = baseAncFloatingMenu.findViewById(R.id.anc_fab);
            if (floatingActionButton != null)
                floatingActionButton.setImageResource(R.drawable.floating_call);
        }
    }

    @Override
    public void setUpComingServicesStatus(String service, AlertStatus status, Date date) {
        view_most_due_overdue_row.setVisibility(View.GONE);
        rlUpcomingServices.setVisibility(View.GONE);
    }

    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PncMemberProfileContract.Presenter) presenter()).fetchReferralTasks();
        if (notificationAndReferralRecyclerView != null && notificationAndReferralRecyclerView.getAdapter() != null) {
            notificationAndReferralRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pnc_member_profile_menu, menu);
        List<ChildModel> childModels = PNCDao.childrenForPncWoman(memberObject.getBaseEntityId());
        for (int i = 0; i < childModels.size(); i++) {
            menu.add(0, R.id.action_pnc_registration, 100 + i, getString(R.string.edit_child_form_title, childModels.get(i).getFirstName()));
            menuItemEditNames.put(getString(R.string.edit_child_form_title, childModels.get(i).getFirstName()), childModels.get(i).getBaseEntityId());
        }
        menu.findItem(R.id.action__pnc_remove_member).setVisible(false);
        menu.findItem(R.id.action__pnc_danger_sign_outcome).setVisible(true);

        if (MalariaDao.isRegisteredForMalaria(baseEntityID)) {
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
            menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        } else {
            menu.findItem(R.id.action_malaria_diagnosis).setVisible(true);
        }
        if (FpDao.isRegisteredForFp(baseEntityID)) {
            menu.findItem(R.id.action_fp_change).setVisible(true);
        } else {
            menu.findItem(R.id.action_fp_initiation_pnc).setVisible(true);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON) {
            String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
            try {
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                    FamilyEventClient familyEventClient =
                            new FamilyProfileModel(memberObject.getFamilyName()).processUpdateMemberRegistration(jsonString, memberObject.getBaseEntityId());
                    new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, (FamilyProfileContract.InteractorCallBack) pncMemberProfilePresenter());
                } else if (encounterType.equals(CoreConstants.EventType.PNC_DANGER_SIGNS_OUTCOME)) {
                    try {
                        getPncMemberProfilePresenter().createPncDangerSignsOutcomeEvent(Utils.getAllSharedPreferences(), jsonString, memberObject.getBaseEntityId());
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
            } catch (JSONException jsonException) {
                Timber.e(jsonException);
            }
        }
    }

    @Override
    public void setupViews() {
        super.setupViews();
    }

    @Override
    public void setFamilyStatus(AlertStatus status) {
        view_family_row.setVisibility(View.GONE);
        rlFamilyServicesDue.setVisibility(View.GONE);
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return FamilyProfileActivity.class;
    }

    @Override
    protected CorePncMemberProfileInteractor getPncMemberProfileInteractor() {
        return new PncMemberProfileInteractor();
    }


    @Override
    protected void removePncMember() {
        //TODO implement functionality to remove PNC member
    }

    @Override
    protected Class<? extends CorePncRegisterActivity> getPncRegisterActivityClass() {
        return PncRegisterActivity.class;
    }

    public PncMemberProfileContract.Presenter pncMemberProfilePresenter() {
        if (pncMemberProfilePresenter == null) {
            pncMemberProfilePresenter = new PncMemberProfilePresenter(this, new PncMemberProfileInteractor(), memberObject);
        }
        return pncMemberProfilePresenter;
    }
}
