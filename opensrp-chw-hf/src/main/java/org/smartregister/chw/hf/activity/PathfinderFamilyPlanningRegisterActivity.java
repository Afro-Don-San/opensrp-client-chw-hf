package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;

import com.adosa.opensrp.chw.fp.util.PathfinderFamilyPlanningConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.activity.CorePathfinderFamilyPlanningRegisterActivity;
import org.smartregister.chw.core.dataloader.FPDataLoader;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.fragment.PathfinderFamilyPlanningRegisterFragment;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class PathfinderFamilyPlanningRegisterActivity extends CorePathfinderFamilyPlanningRegisterActivity {
    private static String baseEntityId;

    public static void startFpRegistrationActivity(Activity activity, String baseEntityID, String dob, String formName, String payloadType) {
        Intent intent = new Intent(activity, PathfinderFamilyPlanningRegisterActivity.class);
        intent.putExtra(PathfinderFamilyPlanningConstants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(PathfinderFamilyPlanningConstants.ActivityPayload.DOB, dob);
        intent.putExtra(PathfinderFamilyPlanningConstants.ActivityPayload.FP_FORM_NAME, formName);
        intent.putExtra(PathfinderFamilyPlanningConstants.ActivityPayload.ACTION, payloadType);
        baseEntityId = baseEntityID;
        activity.startActivity(intent);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    public void onFormSaved() {
        startActivity(new Intent(this, PathfinderFamilyPlanningRegisterActivity.class));
        super.onFormSaved();
        this.finish();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PathfinderFamilyPlanningRegisterFragment();
    }

    @Override
    protected Activity getFpRegisterActivity() {
        return this;
    }

    @Override
    public JSONObject getFpFormForEdit() {

        NativeFormsDataBinder binder = new NativeFormsDataBinder(this, baseEntityId);
        binder.setDataLoader(new FPDataLoader(getString(R.string.fp_update_family_planning)));

        JSONObject form = binder.getPrePopulatedForm(PathfinderFamilyPlanningConstants.Forms.FAMILY_PLANNING_REGISTRATION_FORM);
        try {
            form.put(JsonFormUtils.ENCOUNTER_TYPE, PathfinderFamilyPlanningConstants.EventType.UPDATE_FAMILY_PLANNING_REGISTRATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return form;
    }

}