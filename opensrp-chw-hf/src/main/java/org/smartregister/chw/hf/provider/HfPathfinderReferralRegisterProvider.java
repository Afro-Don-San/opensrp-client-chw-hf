package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.holders.FooterViewHolder;
import org.smartregister.chw.core.holders.ReferralViewHolder;
import org.smartregister.chw.core.provider.BaseReferralRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;

import static org.smartregister.chw.core.utils.Utils.getDuration;

public class HfPathfinderReferralRegisterProvider extends BaseReferralRegisterProvider {
    private Context context;

    public HfPathfinderReferralRegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, onClickListener, paginationClickListener);
        this.context = context;

    }

    @Override
    public void populatePatientColumn(CommonPersonObjectClient pc, ReferralViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);
        String dobString = getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        viewHolder.setName(WordUtils.capitalize(childName) + ", " + WordUtils.capitalize(Utils.getTranslatedDate(dobString, context)));

        String focus = Utils.getValue(pc.getColumnmaps(), CoreConstants.DB_CONSTANTS.FOCUS, true);


        String taskFocus="";
        switch (focus){
            case CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS:
                taskFocus = context.getString(R.string.start_anc_clinic_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.FP_METHOD:
                taskFocus = context.getString(R.string.fp_method_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.PREGNANCY_TEST:
                taskFocus = context.getString(R.string.pregnancy_test_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.SUSPECTED_STI:
                taskFocus = context.getString(R.string.sti_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.SUSPECTED_HIV:
                taskFocus = context.getString(R.string.suspect_hiv_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.CTC_SERVICES:
                taskFocus = context.getString(R.string.htc_referral_reason);
                break;
            default:
                taskFocus = focus;
                break;
        }




        String referredBy = Utils.getValue(pc.getColumnmaps(), CoreConstants.DB_CONSTANTS.OWNER, true);
        viewHolder.setReason(taskFocus);
        viewHolder.setReferredBy(referredBy);

        String executionStart = Utils.getValue(pc.getColumnmaps(), CoreConstants.DB_CONSTANTS.START, false);
        if (StringUtils.isNotBlank(executionStart)) {
            DateTime duration = new DateTime(Long.valueOf(executionStart));
            viewHolder.setReferralStart(org.smartregister.chw.core.utils.Utils.formatReferralDuration(duration, context));
        }
        attachPatientOnclickListener(viewHolder.itemView, pc);


    }

}
