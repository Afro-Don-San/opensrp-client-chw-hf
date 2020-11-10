package org.smartregister.chw.hf.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.StockUsageReportUtils;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.ProvidersReportListAdapter;
import org.smartregister.chw.hf.dao.HfStockUsageReportDao;
import org.smartregister.chw.hf.utils.HfInnAppUtils;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProvidersReportListActivity extends SecuredActivity {
    protected AppBarLayout appBarLayout;
    protected CustomFontTextView toolBarTextView;
    protected String providerType;
    protected StockUsageReportUtils stockUsageReportUtils = new StockUsageReportUtils();
    protected HfStockUsageReportDao hfStockUsageReportDao = new HfStockUsageReportDao();

    protected List<String> getProviderList() {
        List<String> arrayList = new LinkedList<>();
        List<String> providers = new ArrayList<>();
        arrayList.add(this.getString(R.string.all_chw));
        if (stockUsageReportUtils.getPreviousMonths(this).size() > 0) {
            for (Map.Entry<String, String> entry : stockUsageReportUtils.getPreviousMonths(this).entrySet()) {
                providers.addAll(getDBProviders(entry.getKey(), entry.getValue()));
            }
        }
        arrayList.addAll(new HashSet<>(providers));
        return arrayList;
    }

    protected List<String> getDBProviders(String key, String value) {
        if (providerType.equalsIgnoreCase(CoreConstants.HfInAppUtil.PROVIDER_TYPE)) {
            String yearMonth = HfInnAppUtils.getYearMonth(stockUsageReportUtils.getMonthNumber(key.substring(0, 3)), value);
            return hfStockUsageReportDao.getHFListOfProviders(yearMonth, CoreConstants.HfInAppUtil.IN_APP_TABLE_NAME);
        }
        return hfStockUsageReportDao.getListOfProviders(stockUsageReportUtils.getMonthNumber(key.substring(0, 3)), value, CoreConstants.HfStockUsageUtil.STOCK_USAGE_TABLE_NAME);
    }

    protected ProvidersReportListAdapter getAdapter() {
        return new ProvidersReportListAdapter(getProviderList(), this, providerType);
    }

    @Override
    protected void onCreation() {
        Intent intent = getIntent();
        providerType = intent.getStringExtra(CoreConstants.HfInAppUtil.PROVIDER_TYPE) != null ? intent.getStringExtra(CoreConstants.HfInAppUtil.PROVIDER_TYPE) : "stock_usage_providers";
        setContentView(R.layout.activity_provider_stock_usage_list_report);
        RecyclerView recyclerView = findViewById(R.id.rv_provider_stock_usage_report);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Toolbar toolbar = findViewById(R.id.back_to_nav_toolbar);
        toolBarTextView = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            // upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            upArrow.setVisible(true, true);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        toolBarTextView.setOnClickListener(v -> finish());
        if (providerType.equalsIgnoreCase(CoreConstants.HfInAppUtil.PROVIDER_TYPE)) {
            toolBarTextView.setText(this.getString(R.string.review_chw_services));
        }
        else {
            toolBarTextView.setText(this.getString(R.string.stock_usage_title));
        }
        appBarLayout = findViewById(R.id.app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResumption() {
        //override super
    }
}
