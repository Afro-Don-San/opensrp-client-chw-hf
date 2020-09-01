package org.smartregister.chw.hf.model;

import androidx.annotation.NonNull;

import com.adosa.opensrp.chw.fp.model.BaseFpRegisterFragmentModel;
import com.adosa.opensrp.chw.fp.util.PathfinderFamilyPlanningConstants;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class PathfinderFamilyPlanningRegisterFragmentModel extends BaseFpRegisterFragmentModel {

    @NonNull
    @Override
    public String mainSelect(@NonNull String tableName, @NonNull String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("INNER JOIN " + PathfinderFamilyPlanningConstants.DBConstants.FAMILY_MEMBER + " ON  " + tableName + "." + PathfinderFamilyPlanningConstants.DBConstants.BASE_ENTITY_ID + " = " + PathfinderFamilyPlanningConstants.DBConstants.FAMILY_MEMBER + "." + PathfinderFamilyPlanningConstants.DBConstants.BASE_ENTITY_ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + PathfinderFamilyPlanningConstants.DBConstants.FAMILY + " ON  " + PathfinderFamilyPlanningConstants.DBConstants.FAMILY_MEMBER + "." + PathfinderFamilyPlanningConstants.DBConstants.RELATIONAL_ID + " = " + PathfinderFamilyPlanningConstants.DBConstants.FAMILY + "." + PathfinderFamilyPlanningConstants.DBConstants.BASE_ENTITY_ID);
        queryBuilder.customJoin("LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);

        return queryBuilder.mainCondition(mainCondition);
    }
}
