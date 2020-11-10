package org.smartregister.chw.hf.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.chw.core.fragment.BaseReferralRegisterFragment;
import org.smartregister.chw.core.provider.BaseReferralRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.activity.ReferralTaskViewActivity;
import org.smartregister.chw.hf.presenter.ReferralFragmentPresenter;
import org.smartregister.chw.hf.provider.HfPathfinderReferralRegisterProvider;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Task;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Set;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ReferralRegisterFragment extends BaseReferralRegisterFragment {

    public Handler handler = new Handler();
    private ReferralFragmentPresenter referralFragmentPresenter;
    private CommonPersonObjectClient commonPersonObjectClient;

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns, String tableName) {
        HfPathfinderReferralRegisterProvider registerProvider = new HfPathfinderReferralRegisterProvider(getActivity(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }


    @Override
    public void setClient(CommonPersonObjectClient commonPersonObjectClient) {
        setCommonPersonObjectClient(commonPersonObjectClient);
    }

    @Override
    protected String getMainCondition() {
        return "task.business_status = '" + CoreConstants.BUSINESS_STATUS.REFERRED + "' and  ec_family_member_search.date_removed is null";
    }

    @Override
    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    @Override
    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    @Override
    protected void initializePresenter() {
        referralFragmentPresenter = new ReferralFragmentPresenter(this);
        presenter = referralFragmentPresenter;

    }

    @Override
    protected void onViewClicked(View view) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
        referralFragmentPresenter.setBaseEntityId(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false));
        referralFragmentPresenter.fetchClient();

        Task task = getTask(Utils.getValue(client.getColumnmaps(), "_id", false));
        referralFragmentPresenter.setTasksFocus(task.getFocus());
        goToReferralsDetails(client);

    }

    private Task getTask(String taskId) {
        return HealthFacilityApplication.getInstance().getTaskRepository().getTaskByIdentifier(taskId);
    }

    private void goToReferralsDetails(CommonPersonObjectClient client) {
        handler.postDelayed(() -> ReferralTaskViewActivity.startReferralTaskViewActivity(getActivity(), getCommonPersonObjectClient(), getTask(Utils.getValue(client.getColumnmaps(), "_id", false)), CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY), 100);
    }

    @Override
    public void onQRCodeSucessfullyScanned(String qrCode) {
        Timber.e("Coze QR code: %s", qrCode);
        if (StringUtils.isNotBlank(qrCode)) {
            filter(qrCode.replace("-", ""), "", getMainCondition(), true);
//            setUniqueID(qrCode);
        }
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        Timber.e("Coze = " + filterString);
        if (qrCode) {
            getSearchCancelView().setVisibility(isEmpty(filterString) ? View.INVISIBLE : View.VISIBLE);
            if (isEmpty(filterString)) {
                org.smartregister.util.Utils.hideKeyboard(getActivity());
            }

//          this.filters = filterString;
            this.joinTable = joinTableString;
            this.mainCondition = mainConditionString + " AND referral_form_unique_id = '" + filterString + "'";

            countExecute();

            filterandSortExecute();
            setTotalPatients();

        } else {
            super.filter(filterString, joinTableString, mainConditionString, qrCode);
        }
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query;
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                sql = sql.replace("WHERE", String.format(
                        "JOIN %s ON task.%s = %s.%s " +
                                "JOIN %s ON ec_referral.%s = %s.%s  WHERE",
                        CoreConstants.TABLE_NAME.TASK,
                        CoreConstants.DB_CONSTANTS.FOR,
                        CommonFtsObject.searchTableName(tablename),
                        CommonFtsObject.idColumn, "ec_referral", "entity_id", CommonFtsObject.searchTableName(tablename),
                        CommonFtsObject.idColumn));
                Timber.i("FTS query %s", sql);

                clientAdapter.setTotalcount(commonRepository().countSearchIds(sql));
                Timber.v("total count here %s", clientAdapter.getTotalcount());


            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);

                Timber.i(query);
                cursor = commonRepository().rawCustomQueryForAdapter(query);
                cursor.moveToFirst();
                clientAdapter.setTotalcount(cursor.getInt(0));
                Timber.v("total count here %s", clientAdapter.getTotalcount());
            }

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
