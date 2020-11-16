package org.smartregister.chw.hf.interactor;

import android.content.Context;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.interactor.CoreHivProfileInteractor;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.contract.HivProfileContract;
import org.smartregister.chw.hf.dao.HivFollowupFeedbackDao;
import org.smartregister.chw.hf.model.HivTbFollowupFeedbackDetailsModel;
import org.smartregister.chw.hf.model.HivTbReferralTasksAndFollowupFeedbackModel;
import org.smartregister.chw.hiv.contract.BaseHivProfileContract;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.domain.Task;
import org.smartregister.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HfHivProfileInteractor extends CoreHivProfileInteractor implements HivProfileContract.Interactor {

    public HfHivProfileInteractor(Context context) {
        super(context);
    }

    @Override
    public void getReferralTasks(String planId, String baseEntityId, HivProfileContract.InteractorCallback callback) {
        List<HivTbReferralTasksAndFollowupFeedbackModel> tasksAndFollowupFeedbackModels = new ArrayList<>();
        TaskRepository taskRepository = CoreChwApplication.getInstance().getTaskRepository();
        Set<Task> taskList = ((ChwTaskRepository) taskRepository).getReferralTasksForClientByStatus(planId, baseEntityId, CoreConstants.BUSINESS_STATUS.REFERRED);

        for (Task task : taskList) {
            HivTbReferralTasksAndFollowupFeedbackModel tasksAndFollowupFeedbackModel = new HivTbReferralTasksAndFollowupFeedbackModel();
            tasksAndFollowupFeedbackModel.setTask(task);
            tasksAndFollowupFeedbackModel.setType("TASK");
            tasksAndFollowupFeedbackModels.add(tasksAndFollowupFeedbackModel);
        }

        List<HivTbFollowupFeedbackDetailsModel> followupFeedbackList = HivFollowupFeedbackDao.getHivFollowupFeedback(baseEntityId);

        for (HivTbFollowupFeedbackDetailsModel followupFeedbackDetailsModel : followupFeedbackList) {
            HivTbReferralTasksAndFollowupFeedbackModel tasksAndFollowupFeedbackModel = new HivTbReferralTasksAndFollowupFeedbackModel();
            tasksAndFollowupFeedbackModel.setFollowupFeedbackDetailsModel(followupFeedbackDetailsModel);
            tasksAndFollowupFeedbackModel.setType("FOLLOWUP_FEEDBACK");
            tasksAndFollowupFeedbackModels.add(tasksAndFollowupFeedbackModel);
        }


        callback.updateReferralTasksAndFollowupFeedback(tasksAndFollowupFeedbackModels);
    }

    @Override
    public void updateProfileHivStatusInfo(HivMemberObject memberObject, BaseHivProfileContract.InteractorCallback callback) {
        //overriding updateProfileHivStatusInfo
    }
}