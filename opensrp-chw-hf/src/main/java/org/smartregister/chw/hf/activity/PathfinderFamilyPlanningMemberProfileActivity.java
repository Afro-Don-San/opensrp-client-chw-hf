package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.adosa.opensrp.chw.fp.util.PathfinderFamilyPlanningConstants;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.activity.CoreFamilyPlanningMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFpUpcomingServicesActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FpUtil;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.ReferralCardViewAdapter;
import org.smartregister.chw.hf.contract.FamilyPlanningMemberProfileContract;
import org.smartregister.chw.hf.interactor.HfFamilyPlanningProfileInteractor;
import org.smartregister.chw.hf.presenter.HfFamilyPlanningMemberProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;

public class PathfinderFamilyPlanningMemberProfileActivity extends CoreFamilyPlanningMemberProfileActivity implements FamilyPlanningMemberProfileContract.View {

    private CommonPersonObjectClient commonPersonObjectClient;

    public static void startFpMemberProfileActivity(Activity activity, FpMemberObject memberObject) {
        Intent intent = new Intent(activity, PathfinderFamilyPlanningMemberProfileActivity.class);
        passToolbarTitle(activity, intent);
        intent.putExtra(FamilyPlanningConstants.FamilyPlanningMemberObject.MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    public void setReferralTasks(Set<Task> taskList) {
        if (notificationAndReferralRecyclerView != null && taskList.size() > 0) {
            RecyclerView.Adapter mAdapter = new ReferralCardViewAdapter(taskList, this, getCommonPersonObjectClient(), CoreConstants.REGISTERED_ACTIVITIES.FP_REGISTER_ACTIVITY);
            notificationAndReferralRecyclerView.setAdapter(mAdapter);
            notificationAndReferralLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        setCommonPersonObjectClient(getClientDetailsByBaseEntityID(fpMemberObject.getBaseEntityId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((FamilyPlanningMemberProfileContract.Presenter) fpProfilePresenter).fetchReferralTasks();
        if (notificationAndReferralRecyclerView != null && notificationAndReferralRecyclerView.getAdapter() != null) {
            notificationAndReferralRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        fpProfilePresenter = new HfFamilyPlanningMemberProfilePresenter(this, new HfFamilyPlanningProfileInteractor(this), fpMemberObject);
    }

    @Override
    public void openFamilyPlanningRegistration() {
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getPathfinderFamilyPlanningRegistrationForm(), PathfinderFamilyPlanningConstants.ActivityPayload.UPDATE_REGISTRATION_PAYLOAD_TYPE);
    }

    @Override
    public void openUpcomingServices() {
        CoreFpUpcomingServicesActivity.startMe(this, FpUtil.toMember(fpMemberObject));
    }

    @Override
    public void openMedicalHistory() {
        OnMemberTypeLoadedListener onMemberTypeLoadedListener = memberType -> {

            switch (memberType.getMemberType()) {
                case CoreConstants.TABLE_NAME.ANC_MEMBER:
                    AncMedicalHistoryActivity.startMe(PathfinderFamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(PathfinderFamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(PathfinderFamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                default:
                    Timber.v("Member info undefined");
                    break;
            }
        };
        executeOnLoaded(onMemberTypeLoadedListener);
    }

    @Override
    public void updateFollowUpVisitStatusRow(Visit lastVisit) {
        setupFollowupVisitEditViews(false);
        hideFollowUpVisitButton();
    }

    @Override
    protected void removeMember() {
        // Not required for HF (as seen in other profile activities)?
    }

    @Override
    protected void startFamilyPlanningRegistrationActivity() {
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpChangeMethodForm(fpMemberObject.getGender()), FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    public void verifyHasPhone() {
        // TODO -> Implement for HF
    }

    @Override
    public void notifyHasPhone(boolean b) {
        // TODO -> Implement for HF
    }

    @Override
    public String getFpMethodRowString(String fpMethod, String fpStartDate, String fpRegistrationDate) {
        String fpMethodDisplayText;
        String fpDisplayDate = "";
        if (StringUtils.isNotEmpty(fpStartDate) || StringUtils.isNotEmpty(fpRegistrationDate)) {
            if (StringUtils.isNotEmpty(fpStartDate))
                fpDisplayDate = String.valueOf(formatTime(Long.parseLong(fpStartDate)));
            else
                fpDisplayDate = String.valueOf(fpRegistrationDate);
        }
        String insertionText = getString(com.adosa.opensrp.chw.fp.R.string.fp_insertion);
        String startedText = getString(com.adosa.opensrp.chw.fp.R.string.fp_started);
        String onText = getString(com.adosa.opensrp.chw.fp.R.string.fp_on);

        switch (fpMethod) {
            case PathfinderFamilyPlanningConstants.DBConstants.FP_POP:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.pop) + " " + startedText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_COC:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.coc) + " " + startedText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_FEMALE_CONDOM:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.female_condom) + " " + startedText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_MALE_CONDOM:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.male_condom) + " " + startedText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_INJECTABLE:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.injectable) + " " + startedText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_IUD:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.iud) + " " + insertionText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_VASECTOMY:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.vasectomy);
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_TUBAL_LIGATION:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.tubal_ligation);
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_LAM:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.lam) + " " + insertionText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_IMPLANTS:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.implants) + " " + insertionText;
                break;
            case PathfinderFamilyPlanningConstants.DBConstants.FP_SDM:
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.standard_day_method) + " " + startedText;
                break;
            case "0": //TODO coze update empty fp method to ""
                fpMethodDisplayText = getString(com.adosa.opensrp.chw.fp.R.string.registered) + " ";
                break;
            default:
                fpMethodDisplayText = fpMethod + " " + getString(com.adosa.opensrp.chw.fp.R.string.fp_started) + " " + onText;
        }

        return fpMethodDisplayText + " " + onText + " " + fpDisplayDate;
    }

    private CharSequence formatTime(long timestamp) {
        CharSequence timePassedString = null;
        try {
            Date date = new Date(timestamp);
            timePassedString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) {
            Timber.d(e);
        }
        return timePassedString;
    }

    @Override
    public void setUpComingServicesStatus(String service, AlertStatus status, Date date) {
        rlUpcomingServices.setVisibility(View.GONE);
    }
}
