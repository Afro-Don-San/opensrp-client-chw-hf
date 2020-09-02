package org.smartregister.chw.hf.utils;

import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.repository.HfTaskRepository;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;

import java.util.Calendar;
import java.util.List;

public class HfReferralUtils extends CoreReferralUtils {

    public static final String REGISTER_TYPE = "register_type";

    public static void displayReferralDay(CommonPersonObjectClient client, String referralType, TextView textViewReferralDay) {
        Task referralTask = getLatestClientReferralTask(client.entityId(), referralType);
        if (referralTask.getExecutionStartDate() != null) {
            textViewReferralDay.setVisibility(View.VISIBLE);
            String referralDay = textViewReferralDay.getContext().getResources().getString(
                    R.string.referral_day, getReferralPeriod(referralTask.getExecutionStartDate()));
            textViewReferralDay.setText(referralDay);
        } else {
            textViewReferralDay.setVisibility(View.GONE);
        }
    }

    public static void displayReferralDayByReferralTypes(CommonPersonObjectClient client, List<String> referralTypes, TextView textViewReferralDay) {
        boolean hasReferralTasks = false;
        for(String referralType:referralTypes){
            Task referralTask = getLatestClientReferralTask(client.entityId(), referralType);
            if (referralTask.getExecutionStartDate() != null) {
                textViewReferralDay.setVisibility(View.VISIBLE);
                String referralDay = textViewReferralDay.getContext().getResources().getString(
                        R.string.referral_day, getReferralPeriod(referralTask.getExecutionStartDate()));
                textViewReferralDay.setText(referralDay);
                hasReferralTasks = true;
            }
        }
        if (!hasReferralTasks) {
            textViewReferralDay.setVisibility(View.GONE);
        }
    }

    private static String getReferralPeriod(DateTime executionStartDate) {
        String standardDays = String.valueOf(new Duration(executionStartDate,
                new DateTime(Calendar.getInstance().getTime())).abs().toStandardDays());

        return standardDays.toLowerCase().replace("p", "");
    }

    private static Task getLatestClientReferralTask(String baseEntityId, String referralType) {

        return ((HfTaskRepository) HealthFacilityApplication.getInstance()
                .getTaskRepository()).getLatestTaskByEntityId(baseEntityId, referralType);

    }

    public static String getReferralDueFilter(String tableName, String taskFocus) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                " SELECT distinct (task.for) ")
                .append(" FROM task ")
                .append(" INNER JOIN %t ON %t.base_entity_id = task.for ")
                .append(" WHERE task.business_status = 'Referred' ")
                .append("  AND task.status = 'READY' ")
                .append("  AND %t.is_closed is  '0' ");

        if (taskFocus != null) {
            stringBuilder.append("  AND task.focus = '%f'");
        }
        String filterQuery = stringBuilder.toString().replace("%t", tableName);
        if (taskFocus != null) {
            filterQuery = filterQuery.replace("%f", taskFocus);
        }
        return filterQuery;
    }

    public static String getTaskFocus(String registerType) {
        String focus = CoreConstants.REGISTER_TYPE.OTHER;
        if (registerType != null) {
            switch (registerType) {
                case CoreConstants.REGISTER_TYPE.CHILD:
                    focus = CoreConstants.TASKS_FOCUS.SICK_CHILD;
                    break;
                case CoreConstants.REGISTER_TYPE.ANC:
                    focus = CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS;
                    break;
                case CoreConstants.REGISTER_TYPE.PNC:
                    focus = CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS;
                    break;
                case CoreConstants.REGISTER_TYPE.FAMILY_PLANNING:
                    focus = CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS;
                    break;
                default:
                    focus = CoreConstants.REGISTER_TYPE.OTHER;
                    break;
            }
        }
        return focus;
    }
}
