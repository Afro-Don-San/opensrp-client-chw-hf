package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adosa.opensrp.chw.fp.dao.PathfinderFpDao;
import com.adosa.opensrp.chw.fp.domain.PathfinderFpMemberObject;
import com.adosa.opensrp.chw.fp.fragment.BasePathfinderFpRegisterFragment;
import com.adosa.opensrp.chw.fp.util.PathfinderFamilyPlanningConstants;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.core.provider.CorePathfinderFpProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.utils.HfReferralUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.util.Utils.getName;

/**
 * Created by cozej4 on 4/28/20.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class HfPathfinderFpProvider extends CorePathfinderFpProvider {

    private final LayoutInflater inflater;
    private Context context;
    private View.OnClickListener onClickListener;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public HfPathfinderFpProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, visibleColumns, onClickListener, paginationClickListener);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.family_planning_register_list_row, parent, false);
        return new HfRegisterViewHolder(view);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, RegisterViewHolder viewHolder) {

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        PathfinderFpMemberObject pathfinderFpMemberObject = PathfinderFpDao.getMember(pc.getCaseId());

        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, pathfinderFpMemberObject, viewHolder);
        }

        viewHolder.dueButton.setVisibility(View.GONE);
        viewHolder.dueButton.setOnClickListener(null);


        showLatestPncReferralDay((CommonPersonObjectClient) smartRegisterClient, (HfRegisterViewHolder) viewHolder);
    }

    private void showLatestPncReferralDay(CommonPersonObjectClient client, HfRegisterViewHolder viewHolder) {
        List<String> referralTypes = new ArrayList<>();
        referralTypes.add(CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS);
        referralTypes.add(CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS);
        referralTypes.add(CoreConstants.TASKS_FOCUS.PREGNANCY_TEST);
        referralTypes.add(CoreConstants.TASKS_FOCUS.FP_METHOD);

        HfReferralUtils.displayReferralDayByReferralTypes(client, referralTypes, viewHolder.textViewReferralDay);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, PathfinderFpMemberObject pathfinderFpMemberObject, final RegisterViewHolder viewHolder) {
        try {
            String firstName = getName(
                    pathfinderFpMemberObject.getFirstName(),
                    pathfinderFpMemberObject.getMiddleName());

            String dobString = Utils.getValue(pc.getColumnmaps(), PathfinderFamilyPlanningConstants.DBConstants.DOB, false);
            int age = new Period(new DateTime(dobString), new DateTime()).getYears();

            String patientName = getName(firstName, pathfinderFpMemberObject.getLastName());
            String methodAccepted = com.adosa.opensrp.chw.fp.util.FpUtil.getTranslatedMethodValue(pathfinderFpMemberObject.getFpMethod(), context);

            viewHolder.patientName.setText(patientName + ", " + age);

            ((HfRegisterViewHolder) viewHolder).hfTextViewFpMethod.setText(methodAccepted);

            if (methodAccepted.equals("") || methodAccepted.equals("0")) {
                viewHolder.textViewFpMethod.setVisibility(View.GONE);
            }

            viewHolder.textViewVillage.setText(pathfinderFpMemberObject.getAddress());
            viewHolder.patientColumn.setOnClickListener(onClickListener);
            viewHolder.patientColumn.setTag(pc);
            viewHolder.patientColumn.setTag(com.adosa.opensrp.chw.fp.R.id.VIEW_ID, BasePathfinderFpRegisterFragment.CLICK_VIEW_NORMAL);

            viewHolder.dueButton.setOnClickListener(onClickListener);
            viewHolder.dueButton.setTag(pc);
            viewHolder.dueButton.setTag(com.adosa.opensrp.chw.fp.R.id.VIEW_ID, BasePathfinderFpRegisterFragment.FOLLOW_UP_VISIT);
            viewHolder.registerColumns.setOnClickListener(onClickListener);

            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.patientColumn.performClick());
            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.dueButton.performClick());

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public class HfRegisterViewHolder extends RegisterViewHolder {

        TextView textViewReferralDay;
        TextView hfTextViewFpMethod;

        public HfRegisterViewHolder(View itemView) {
            super(itemView);
            textViewReferralDay = itemView.findViewById(R.id.text_view_referral_day);
            hfTextViewFpMethod = itemView.findViewById(R.id.text_view_fp_method);
        }
    }
}
