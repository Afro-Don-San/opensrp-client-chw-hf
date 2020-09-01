package org.smartregister.chw.hf.fragment;

import android.widget.TextView;

import com.vijay.jsonwizard.utils.FormUtils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.smartregister.chw.core.fragment.CoreHivCommunityFollowupRegisterFragment;
import org.smartregister.chw.core.provider.CoreHivProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.HivProfileActivity;
import org.smartregister.chw.hf.activity.HivRegisterActivity;
import org.smartregister.chw.hf.model.HivFollowupFragmentModel;
import org.smartregister.chw.hf.presenter.HivFollowupFragmentPresenter;
import org.smartregister.chw.hf.provider.HfHivFollowupProvider;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

public class HivFollowupFragment extends CoreHivCommunityFollowupRegisterFragment {

    @Override
    public void initializeAdapter(@Nullable Set<? extends View> visibleColumns) {
        CoreHivProvider hivRegisterProvider = new HfHivFollowupProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, hivRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        TextView dueOnlyTextView = view.findViewById(R.id.due_only_text_view);
        dueOnlyTextView.setText(getString(R.string.feedback_due_only));
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
        presenter = new HivFollowupFragmentPresenter(this, new HivFollowupFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if (getActivity() != null)
            HivProfileActivity.startHivProfileActivity(getActivity(), Objects.requireNonNull(HivDao.getCommunityFollowupMember(client.getCaseId())));
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


