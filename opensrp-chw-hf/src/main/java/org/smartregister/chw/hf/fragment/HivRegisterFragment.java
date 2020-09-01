package org.smartregister.chw.hf.fragment;

import com.vijay.jsonwizard.utils.FormUtils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.smartregister.chw.core.fragment.CoreHivRegisterFragment;
import org.smartregister.chw.core.provider.CoreHivProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.activity.HivProfileActivity;
import org.smartregister.chw.hf.activity.HivRegisterActivity;
import org.smartregister.chw.hf.model.HivRegisterFragmentModel;
import org.smartregister.chw.hf.presenter.HivRegisterFragmentPresenter;
import org.smartregister.chw.hf.provider.HfHivRegisterProvider;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

public class HivRegisterFragment extends CoreHivRegisterFragment {

    @Override
    public void initializeAdapter(@Nullable Set<? extends View> visibleColumns) {
        CoreHivProvider hivRegisterProvider = new HfHivRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, hivRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((HivRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new HivRegisterFragmentPresenter(this, new HivRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if (getActivity() != null)
            HivProfileActivity.startHivProfileActivity(getActivity(), Objects.requireNonNull(HivDao.getMember(client.getCaseId())));
    }


    @Override
    protected void openFollowUpVisit(@Nullable HivMemberObject hivMemberObject) {
        if (getActivity() != null) {
            try {
                HivRegisterActivity.startHIVFormActivity(getActivity(), hivMemberObject.getBaseEntityId(), CoreConstants.JSON_FORM.getHivRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(getActivity(), CoreConstants.JSON_FORM.getHivFollowupVisit()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}


