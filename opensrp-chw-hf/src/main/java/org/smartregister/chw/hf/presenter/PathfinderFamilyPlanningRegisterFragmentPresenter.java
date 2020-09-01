package org.smartregister.chw.hf.presenter;

import com.adosa.opensrp.chw.fp.contract.BaseFpRegisterFragmentContract;
import com.adosa.opensrp.chw.fp.presenter.BasePathfinderFpRegisterFragmentPresenter;

public class PathfinderFamilyPlanningRegisterFragmentPresenter extends BasePathfinderFpRegisterFragmentPresenter {

    public PathfinderFamilyPlanningRegisterFragmentPresenter(BaseFpRegisterFragmentContract.View view,
                                                             BaseFpRegisterFragmentContract.Model model) {
        super(view, model);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_family_planning.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}
