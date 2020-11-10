package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.activity.BaseReferralTaskViewActivity;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.utils.AllClientsUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;

public class ReferralTaskViewActivity extends BaseReferralTaskViewActivity implements View.OnClickListener {

    JSONObject serviceProvided;
    JSONArray servicesArray = new JSONArray();

    public static void startReferralTaskViewActivity(Activity activity, CommonPersonObjectClient personObjectClient, Task task, String startingActivity) {
        ReferralTaskViewActivity.personObjectClient = personObjectClient;
        Intent intent = new Intent(activity, ReferralTaskViewActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.USERS_TASKS, task);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, personObjectClient);
        intent.putExtra(CoreConstants.INTENT_KEY.STARTING_ACTIVITY, startingActivity);
        passToolbarTitle(activity, intent);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referrals_tasks_view_layout);
        if (getIntent().getExtras() != null) {
            extraClientTask();
            extraDetails();
            setStartingActivity((String) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.STARTING_ACTIVITY));
            inflateToolbar();
            setUpViews();
        }
    }

    @Override
    protected void onCreation() {
        //overridden
    }

    @Override
    protected void onResumption() {
        //Overridden
    }

    public void setUpViews() {
        clientName = findViewById(R.id.client_name);
        careGiverName = findViewById(R.id.care_giver_name);
        childName = findViewById(R.id.child_name);
        careGiverPhone = findViewById(R.id.care_giver_phone);
        clientReferralProblem = findViewById(R.id.client_referral_problem);
        chwDetailsNames = findViewById(R.id.chw_details_names);
        referralDate = findViewById(R.id.referral_date);

        womanGaLayout = findViewById(R.id.woman_ga_layout);
        careGiverLayout = findViewById(R.id.care_giver_name_layout);
        childNameLayout = findViewById(R.id.child_name_layout);

        womanGa = findViewById(R.id.woman_ga);
        CustomFontTextView viewProfile = findViewById(R.id.view_profile);

        CustomFontTextView markAskDone = findViewById(R.id.mark_ask_done);
        markAskDone.setOnClickListener(this);

        if (getStartingActivity().equals(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY)) {
            viewProfile.setOnClickListener(this);
        } else {
            viewProfile.setVisibility(View.INVISIBLE);
        }
        getReferralDetails();
    }

    public void setStartingActivity(String startingActivity) {
        this.startingActivity = startingActivity;
    }

    public void closeReferral() {
        closeReferralDialog();
    }

    private void closeReferralDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.mark_as_done_title));
        final View customLayout = getLayoutInflater().inflate(R.layout.dialogue_close_referral, null);
        builder.setView(customLayout);

        Spinner servicesSpinner = (Spinner) customLayout.findViewById(R.id.spinner);
        JSONObject services;
        List<String> servicesList = new ArrayList<String>();
        try {
            services = (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, "pathfinder_fp_services");
            servicesArray = services.getJSONArray("fp_service");

            if (servicesArray != null) {
                for (int i = 0; i < servicesArray.length(); i++) {
                    servicesList.add(servicesArray.getJSONObject(i).getString("text"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Spinner click listener
        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    serviceProvided = servicesArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, servicesList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        servicesSpinner.setAdapter(dataAdapter);

        builder.setCancelable(true);

        builder.setPositiveButton(this.getString(R.string.mark_done), (dialog, id) -> {
            try {
                saveCloseReferralEvent();
                completeTask();
                finish();
            } catch (Exception e) {
                Timber.e(e, "ReferralTaskViewActivity --> closeReferralDialog");
            }
        });
        builder.setNegativeButton(this.getString(R.string.cancel), ((dialog, id) -> dialog.cancel()));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveCloseReferralEvent() {
        try {
            AllSharedPreferences sharedPreferences = Utils.getAllSharedPreferences();
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();
            Event baseEvent = (Event) new Event()
                    .withBaseEntityId(getBaseEntityId())
                    .withEventDate(new Date())
                    .withEventType(CoreConstants.EventType.CLOSE_REFERRAL)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withEntityType(CoreConstants.TABLE_NAME.CLOSE_REFERRAL)
                    .withProviderId(sharedPreferences.fetchRegisteredANM())
                    .withLocationId(getTask().getLocation())
                    .withTeamId(sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM()))
                    .withTeam(sharedPreferences.fetchDefaultTeam(sharedPreferences.fetchRegisteredANM()))
                    .withClientDatabaseVersion(BuildConfig.DATABASE_VERSION)
                    .withClientApplicationVersion(BuildConfig.VERSION_CODE)
                    .withDateCreated(new Date());

            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK).withValue(getTask().getIdentifier())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));
            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_STATUS).withValue(getTask().getStatus())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_STATUS).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));
            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS).withValue(getTask().getBusinessStatus())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            baseEvent.addObs((new Obs()).withFormSubmissionField("service_provided").withValue(serviceProvided.getString("key"))
                    .withFieldCode("service_provided").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(Arrays.asList(new String[]{serviceProvided.getString("text")})));

            org.smartregister.chw.hf.utils.JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), baseEvent);// tag docs

            //setting the location uuid of the referral initiator so that to allow the event to sync back to the chw app since it sync data by location.
            baseEvent.setLocationId(getTask().getLocation());

            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(getBaseEntityId(), eventJson);
            long lastSyncTimeStamp = HealthFacilityApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            HealthFacilityApplication.getClientProcessor(HealthFacilityApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            HealthFacilityApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "ReferralTaskViewActivity --> saveCloseReferralEvent");
        }

    }

    private void completeTask() {
        Task currentTask = getTask();
        currentTask.setForEntity(getBaseEntityId());
        currentTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        CoreReferralUtils.completeTask(currentTask, false);
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_profile) {
            personObjectClient.getDetails().put(OpdDbConstants.KEY.REGISTER_TYPE, mapTaskFocusToRegisterType());
            try {
                AllClientsUtils.goToClientProfile(this, personObjectClient);
            } catch (Exception e) {
                Timber.e(e);
            }
        } else if (view.getId() == R.id.mark_ask_done) {
            closeReferral();
        }
    }

    @NonNull
    private String mapTaskFocusToRegisterType() {
        switch (task.getFocus()) {
            case CoreConstants.TASKS_FOCUS.SICK_CHILD:
                return CoreConstants.REGISTER_TYPE.CHILD;
            case CoreConstants.TASKS_FOCUS.SUSPECTED_MALARIA:
                return CoreConstants.REGISTER_TYPE.MALARIA;
            case CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS:
                return CoreConstants.REGISTER_TYPE.ANC;
            case CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS:
                return CoreConstants.REGISTER_TYPE.PNC;
            case CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS:
                return CoreConstants.REGISTER_TYPE.FAMILY_PLANNING;
            default:
                return "";
        }
    }

    @Override
    protected void getReferralDetails() {
        if (getPersonObjectClient() != null && getTask() != null) {
            clientReferralProblem.setText(getTask().getFocus());
            String clientAge = (Utils.getTranslatedDate(Utils.getDuration(Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.DOB, false)), getBaseContext()));
            clientName.setText(getString(org.smartregister.chw.core.R.string.client_name_age_suffix, name, clientAge));
            referralDate.setText(org.smartregister.chw.core.utils.Utils.dd_MMM_yyyy.format(getTask().getExecutionStartDate().toDate()));

            //Hide Care giver for clients other than CHILD
            careGiverLayout.setVisibility(View.GONE);
            if (getTask().getFocus().equalsIgnoreCase(CoreConstants.TASKS_FOCUS.SICK_CHILD)) {
                careGiverLayout.setVisibility(View.VISIBLE);
                String parentFirstName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
                String parentLastName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
                String parentMiddleName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);
                String parentName = getString(org.smartregister.chw.core.R.string.care_giver_prefix, org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName));
                careGiverName.setText(parentName);
            }

            String familyMemberContacts = getFamilyMemberContacts();
            careGiverPhone.setText(familyMemberContacts.isEmpty() ? getString(org.smartregister.chw.core.R.string.phone_not_provided) : familyMemberContacts);

            chwDetailsNames.setText(getTask().getRequester());

//            addGaDisplay();
        }
    }
}