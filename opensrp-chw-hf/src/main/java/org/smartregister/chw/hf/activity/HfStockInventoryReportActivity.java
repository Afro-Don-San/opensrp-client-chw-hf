package org.smartregister.chw.hf.activity;

import android.content.Intent;

import org.smartregister.chw.core.activity.CoreStockInventoryReportActivity;
import org.smartregister.chw.core.dao.StockUsageReportDao;
import org.smartregister.chw.core.model.StockUsageItemModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.StockUsageReportUtils;
import org.smartregister.chw.hf.R;

import java.util.ArrayList;
import java.util.List;

public class HfStockInventoryReportActivity extends CoreStockInventoryReportActivity {
    private String providerName;

    @Override
    protected void onCreation() {
        super.onCreation();
        Intent intent = getIntent();
        providerName = intent.getStringExtra(CoreConstants.HfStockUsageUtil.PROVIDER_NAME);
    }

    @Override
    public List<StockUsageItemModel> getStockUsageItemReportList(String month, String year) {
        List<StockUsageItemModel> stockUsageItemModelsList = new ArrayList<>();
        for (String item : getItems()) {
            String usage = providerName.equalsIgnoreCase(this.getString(R.string.all_chw)) ? StockUsageReportDao.getAllStockUsageForMonth(month, item, year) : StockUsageReportDao.getStockUsageForMonth(month, item, year, providerName);
            stockUsageItemModelsList.add(new StockUsageItemModel(StockUsageReportUtils.getFormattedItem(item, this), StockUsageReportUtils.getUnitOfMeasure(item, this), usage, providerName));
        }
        return stockUsageItemModelsList;
    }
}
