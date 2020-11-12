package org.smartregister.chw.hf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.holder.ReferralCardViewHolder;
import org.smartregister.chw.hf.listener.ReferralRecyclerClickListener;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wizard on 06/08/19.
 */
public class PathfinderReferralCardViewAdapter extends RecyclerView.Adapter<ReferralCardViewHolder> {
    private List<Task> tasks;
    private CommonPersonObjectClient personObjectClient;
    private Activity context;
    private String startingActivity;

    public PathfinderReferralCardViewAdapter(Set<Task> taskList, Activity activity, CommonPersonObjectClient personObjectClient, String startingActivity) {
        this.tasks = new ArrayList<>(taskList);
        this.context = activity;
        this.personObjectClient = personObjectClient;
        this.startingActivity = startingActivity;
    }

    @NonNull
    @Override
    public ReferralCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View referralLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_referral_card_row, viewGroup, false);
        return new ReferralCardViewHolder(referralLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralCardViewHolder referralCardViewHolder, int position) {
        Task task = tasks.get(position);
        ReferralRecyclerClickListener referralRecyclerClickListener = new ReferralRecyclerClickListener();
        referralRecyclerClickListener.setTask(task);
        referralRecyclerClickListener.setCommonPersonObjectClient(personObjectClient);
        referralRecyclerClickListener.setActivity(context);
        referralRecyclerClickListener.setStartingActivity(startingActivity);


        String taskFocus="";
        switch (task.getFocus()){
            case CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS:
                taskFocus = context.getString(org.smartregister.chw.core.R.string.start_anc_clinic_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.FP_METHOD:
                taskFocus = context.getString(org.smartregister.chw.core.R.string.fp_method_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.PREGNANCY_TEST:
                taskFocus = context.getString(org.smartregister.chw.core.R.string.pregnancy_test_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.SUSPECTED_STI:
                taskFocus = context.getString(org.smartregister.chw.core.R.string.sti_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.SUSPECTED_HIV:
                taskFocus = context.getString(org.smartregister.chw.core.R.string.htc_referral_reason);
                break;
            case CoreConstants.TASKS_FOCUS.CTC_SERVICES:
                taskFocus = context.getString(org.smartregister.chw.core.R.string.suspect_hiv_referral_reason);
                break;
            default:
                taskFocus = tasks.get(position).getFocus();
                break;
        }


        referralCardViewHolder.textViewReferralHeader.setText(String.format(context.getResources().getString(R.string.referral_for_text),taskFocus));
        referralCardViewHolder.referralRow.setOnClickListener(referralRecyclerClickListener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
