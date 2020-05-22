package org.smartregister.chw.hf.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.chw.hf.activity.FamilyPlanningMemberProfileActivity;
import org.smartregister.chw.hf.activity.MalariaProfileActivity;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.utils.OpdDbConstants;

import static org.smartregister.chw.core.utils.CoreConstants.INTENT_KEY.CLIENT;
import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;
import static org.smartregister.util.Utils.showShortToast;

public class AllClientsUtils {

    public static void goToChildProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        String dobString = Utils.getDuration(Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false));
        Integer yearOfBirth = CoreChildUtils.dobStringToYear(dobString);
        Intent intent;
        if (yearOfBirth != null && yearOfBirth >= 5) {
            intent = new Intent(activity, AboveFiveChildProfileActivity.class);
        } else {
            intent = new Intent(activity, ChildProfileActivity.class);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        passToolbarTitle(activity, intent);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        activity.startActivity(intent);
    }

    public static void gotToPncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.pncRegisterRepository().getPncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, PncMemberProfileActivity.class));
    }

    public static void goToAncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.ancRegisterRepository().getAncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, AncMemberProfileActivity.class));
    }

    public static void gotToMalariaProfile(Activity activity, CommonPersonObjectClient patient) {
        MalariaProfileActivity.startMalariaActivity(activity, patient.getCaseId());
    }

    public static void goToFamilyPlanningProfile(Activity activity, CommonPersonObjectClient patient) {
        FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(activity, FpDao.getMember(patient.getCaseId()));
    }

    private static Intent initProfileActivityIntent(Activity activity, CommonPersonObjectClient patient, Bundle bundle, Class clazz) {
        Intent intent = new Intent(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, patient.entityId());
        intent.putExtra(CLIENT, patient);
        passToolbarTitle(activity, intent);
        return intent;
    }

    public static void goToOtherMemberProfile(Activity activity, CommonPersonObjectClient patient,
                                              Bundle bundle, String familyHead, String primaryCaregiver) {

        if (StringUtils.isBlank(familyHead) && StringUtils.isBlank(primaryCaregiver)) {
            showShortToast(activity, activity.getString(R.string.error_opening_profile));
        } else {
            Intent intent = new Intent(activity, FamilyOtherMemberProfileActivity.class);
            intent.putExtras(bundle != null ? bundle : new Bundle());
            intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
            intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
            intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
            intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
            intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, patient.getDetails().get(OpdDbConstants.KEY.HOME_ADDRESS));
            passToolbarTitle(activity, intent);
            activity.startActivity(intent);
        }
    }

}
