package org.smartregister.chw.hf.activity;

import android.content.Intent;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.smartregister.chw.core.activity.CoreStockInventoryReportActivity;
import org.smartregister.chw.core.model.MonthStockUsageModel;
import org.smartregister.chw.core.model.StockUsageItemModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.StockUsageReportUtils;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.HfStockUsageItemAdapter;
import org.smartregister.chw.hf.dao.HfStockUsageReportDao;
import org.smartregister.chw.hf.utils.HfInnAppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InAppInventoryReportActivity extends CoreStockInventoryReportActivity implements HfStockUsageItemAdapter.Paginator {
    private String providerName;
    private int currentLoad = 1;
    private int itemsPerPage = 10;
    private List<StockUsageItemModel> items = new ArrayList<>();
    private List<StockUsageItemModel> paginatedItems = new ArrayList<>();
    private HfStockUsageItemAdapter hfStockUsageItemAdapter;

    private static Map<String,String> getIndicatorCodes() {
     return HfStockUsageReportDao.getIndicators();
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        Intent intent = getIntent();
        providerName = intent.getStringExtra(CoreConstants.HfStockUsageUtil.PROVIDER_NAME);
        toolBarTextView.setText(getString(org.smartregister.chw.core.R.string.service_review));

    }

    @Override
    public List<StockUsageItemModel> getStockUsageItemReportList(String month, String year) {
        List<StockUsageItemModel> stockUsageItemModelsList = new ArrayList<>();
       if(getItems().size() > 0){
           for (Map.Entry<String, String> entry : getIndicatorCodes().entrySet()) {
               String usage = providerName.equalsIgnoreCase(this.getString(R.string.all_chw)) ? HfStockUsageReportDao.getAllProvidersMonthsValue(HfInnAppUtils.getYearMonth(month, year), entry.getKey()) : HfStockUsageReportDao.getProvidersMonthsValue(HfInnAppUtils.getYearMonth(month, year), entry.getKey(), providerName);
               String name = HfInnAppUtils.getStringResource(this, entry.getValue());
               stockUsageItemModelsList.add(new StockUsageItemModel(name, "", usage, providerName));
           }
       }
        return stockUsageItemModelsList;
    }


    @Override
    protected void reloadRecycler(MonthStockUsageModel selected) {
        String stockMonth = StockUsageReportUtils.getMonthNumber(selected.getMonth().substring(0, 3));
        String stockYear = selected.getYear();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        items = getStockUsageItemReportList(stockMonth, stockYear);
        hfStockUsageItemAdapter = new HfStockUsageItemAdapter(this, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(hfStockUsageItemAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        startPagination();
    }

    private void startPagination() {
        paginatedItems.clear();
        int start = (currentLoad - 1) * itemsPerPage;
        int end = currentLoad * itemsPerPage;

        if (end > items.size())
            end = items.size();

        while (start < end) {
            paginatedItems.add(items.get(start));
            start++;
        }

        hfStockUsageItemAdapter.setStockUsageItemModelList(paginatedItems, this);
        hfStockUsageItemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean hasPagination() {
        return items.size() > 0;
    }

    @Override
    public int currentPage() {
        return currentLoad;
    }

    @Override
    public int totalPages() {
        return items.size() / itemsPerPage + (items.size() % itemsPerPage > 0 ? 1 : 0);
    }

    @Override
    public boolean hasNext() {
        return currentPage() < totalPages();
    }

    @Override
    public boolean hasPrevious() {
        return currentPage() > 1;
    }

    @Override
    public void onNextNavigation() {
        // fetch next
        if (currentLoad < totalPages()) {
            currentLoad++;
            startPagination();
        }
    }

    @Override
    public void onPreviousNavigation() {
        // fetch previous
        if (currentLoad > 1) {
            currentLoad--;
            startPagination();
        }
    }
}
