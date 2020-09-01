package org.smartregister.chw.hf.fragment;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.smartregister.chw.core.fragment.CoreTbCommunityFollowupRegisterFragment;
import org.smartregister.chw.core.provider.CoreTbProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.TbProfileActivity;
import org.smartregister.chw.hf.activity.TbRegisterActivity;
import org.smartregister.chw.hf.model.TbFollowupFragmentModel;
import org.smartregister.chw.hf.presenter.TbFollowupFragmentPresenter;
import org.smartregister.chw.hf.provider.HfTbFollowupProvider;
import org.smartregister.chw.tb.dao.TbDao;
import org.smartregister.chw.tb.domain.TbMemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

public class TbFollowupFragment extends CoreTbCommunityFollowupRegisterFragment {

    @Override
    public void initializeAdapter(@org.jetbrains.annotations.Nullable Set<? extends View> visibleColumns) {
        CoreTbProvider tbRegisterProvider = new HfTbFollowupProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, tbRegisterProvider, context().commonrepository(this.tablename));
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
            viewConfigurationIdentifier = ((TbRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new TbFollowupFragmentPresenter(this, new TbFollowupFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if (getActivity() != null)
            TbProfileActivity.startTbProfileActivity(getActivity(), Objects.requireNonNull(TbDao.getCommunityFollowupMember(client.getCaseId())));
    }

    @Override
    protected void openFollowUpVisit(@Nullable TbMemberObject tbMemberObject) {
        if (getActivity() != null) {
            try {
                TbRegisterActivity.startTbFormActivity(getActivity(), tbMemberObject.getBaseEntityId(), CoreConstants.JSON_FORM.getTbFollowupVisit(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(getActivity(), CoreConstants.JSON_FORM.getTbFollowupVisit()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}


