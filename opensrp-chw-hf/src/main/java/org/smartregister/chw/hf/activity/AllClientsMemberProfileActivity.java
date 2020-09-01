package org.smartregister.chw.hf.activity;

import android.content.Context;
import android.view.Menu;

import androidx.viewpager.widget.ViewPager;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.activity.CoreAllClientsMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.contract.CoreAllClientsMemberContract;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.hf.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.hf.fragment.FamilyOtherMemberProfileFragment;
import org.smartregister.chw.hf.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.chw.hf.presenter.HfAllClientsMemberPresenter;
import org.smartregister.chw.hf.utils.AllClientsUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.view.contract.BaseProfileContract;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.COUNT;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.smartregister.family.util.JsonFormUtils.STEP2;

public class AllClientsMemberProfileActivity extends CoreAllClientsMemberProfileActivity {

    private FamilyMemberFloatingMenu familyFloatingMenu;
    private CoreAllClientsMemberContract.Presenter allClientsMemberPresenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_location_info).setVisible(true);

        if(BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH) {
            AllClientsUtils.updateHivMenuItems(baseEntityId, menu);
            AllClientsUtils.updateTbMenuItems(baseEntityId, menu);
        }
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        return true;
    }


    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }

    @Override
    protected void startAncRegister() {
        AncRegisterActivity.startAncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber,
                CoreConstants.JSON_FORM.getAncRegistration(), null, familyBaseEntityId, familyName);
    }

    @Override
    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId);

    }

    @Override
    protected void startHivRegister() {
        try {
            HivRegisterActivity.startHIVFormActivity(AllClientsMemberProfileActivity.this, baseEntityId, CoreConstants.JSON_FORM.getHivRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getHivRegistration()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void startTbRegister() {
        try {
            TbRegisterActivity.startTbFormActivity(AllClientsMemberProfileActivity.this, baseEntityId, CoreConstants.JSON_FORM.getTbRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getTbRegistration()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void startFpRegister() {
        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, dob, CoreConstants.JSON_FORM.getFpRegistrationForm(gender),
                FamilyPlanningConstants.ActivityPayload.REGISTRATION_PAYLOAD_TYPE);
    }


    @Override
    protected void startFpChangeMethod() {
        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, dob, CoreConstants.JSON_FORM.getFpChangeMethodForm(gender),
                FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    protected void removeIndividualProfile() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(AllClientsMemberProfileActivity.this,
                commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver, AllClientsRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void startEditMemberJsonForm(Integer title_resource, CommonPersonObjectClient client) {
        String titleString = title_resource != null ? getResources().getString(title_resource) : null;
        CommonPersonObjectClient commonPersonObjectClient = getFamilyRegistrationDetails();
        String uniqueID = commonPersonObjectClient.getColumnmaps().get(DBConstants.KEY.UNIQUE_ID);
        boolean isPrimaryCareGiver = commonPersonObject.getCaseId().equalsIgnoreCase(primaryCaregiver);

        NativeFormsDataBinder binder = new NativeFormsDataBinder(getContext(), commonPersonObject.getCaseId());
        binder.setDataLoader(new FamilyMemberDataLoader(familyName, isPrimaryCareGiver, titleString,
                Utils.metadata().familyMemberRegister.updateEventType, uniqueID));
        JSONObject jsonObject = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getAllClientRegistrationForm());

        try {
            //Remove the first step and use the updated one
            if (jsonObject != null && jsonObject.has(STEP1)) {

                jsonObject.put(JsonFormUtils.ENTITY_ID, baseEntityId);
                jsonObject.put(COUNT, "1");
                jsonObject.remove(STEP1);
                jsonObject.put(STEP1, jsonObject.getJSONObject(STEP2));
                jsonObject.remove(STEP2);
                startFormActivity(jsonObject);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseProfileContract.Presenter getFamilyOtherMemberActivityPresenter(
            String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        return new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(),
                null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected FamilyMemberFloatingMenu getFamilyMemberFloatingMenu() {
        if (familyFloatingMenu == null) {
            familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        }
        return familyFloatingMenu;
    }

    @Override
    protected Context getFamilyOtherMemberProfileActivity() {
        return AllClientsMemberProfileActivity.this;
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivity() {
        return FamilyProfileActivity.class;
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        onClickFloatingMenu = this;
        allClientsMemberPresenter = new HfAllClientsMemberPresenter(this, baseEntityId);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected BaseFamilyOtherMemberProfileFragment getFamilyOtherMemberProfileFragment() {
        return FamilyOtherMemberProfileFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void startMalariaFollowUpVisit() {
        // TODO -> Implement for HF
    }

    @Override
    protected void setIndependentClient(boolean isIndependentClient) {
        super.isIndependent = isIndependentClient;
    }

    @Override
    public void onClickMenu(int viewId) {
        if (viewId == R.id.call_layout) {
            FamilyCallDialogFragment.launchDialog(this, familyBaseEntityId);
        }
    }

    @Override
    public CoreAllClientsMemberContract.Presenter getAllClientsMemberPresenter() {
        return allClientsMemberPresenter;
    }


}
