package org.smartregister.chw.hf.fragment;

import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.chw.core.provider.CoreFpProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.activity.FamilyPlanningMemberProfileActivity;
import org.smartregister.chw.hf.activity.PathfinderFamilyPlanningMemberProfileActivity;
import org.smartregister.chw.hf.model.FpRegisterFragmentModel;
import org.smartregister.chw.hf.presenter.FpRegisterFragmentPresenter;
import org.smartregister.chw.hf.provider.HfFpRegisterProvider;
import org.smartregister.chw.hf.utils.HfReferralUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class FpRegisterFragment extends CoreFpRegisterFragment {

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        CoreFpProvider fpRegisterProvider = new HfFpRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, fpRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new FpRegisterFragmentPresenter(this, new FpRegisterFragmentModel());
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if(BuildConfig.BUILD_FOR_PATHFINDER){
            PathfinderFamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(getActivity(), FpDao.getMember(client.getCaseId()));
        }else {
            FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(getActivity(), FpDao.getMember(client.getCaseId()));
        }

    }

    @Override
    public String getDueCondition() {
        return " " + CoreConstants.TABLE_NAME.FP_MEMBER + ".base_entity_id in ("
                + HfReferralUtils.getReferralDueFilter(CoreConstants.TABLE_NAME.FP_MEMBER, CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS)
                + ")";
    }
}
