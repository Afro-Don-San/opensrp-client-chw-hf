package org.smartregister.chw.hf.custom_view;

import android.app.Activity;
import android.content.Intent;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.activity.HfHIA2ReportsActivity;
import org.smartregister.chw.hf.activity.ProvidersReportListActivity;
import org.smartregister.chw.hf.activity.ServiceActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HfNavigationMenu implements NavigationMenu.Flavour {

    @Override
    public List<Pair<String, Locale>> getSupportedLanguages() {
        return Arrays.asList(Pair.of("English", Locale.ENGLISH), Pair.of("Kiswahili", new Locale("sw")));
    }

    @Override
    public HashMap<String, String> getTableMapValues() {
        HashMap<String, String> tableMap = new HashMap<>();
        tableMap.put(CoreConstants.DrawerMenu.REFERRALS, CoreConstants.TABLE_NAME.TASK);
        return tableMap;
    }

    @Override
    public boolean hasServiceReport() {
        return false;
    }

    @Override
    public boolean hasStockReport() {
        return false;
    }

    @Override
    public boolean hasCommunityResponders() {
        return false;
    }

    @Override
    public Intent getStockReportIntent(Activity activity) {
        return new Intent(activity, ProvidersReportListActivity.class);
    }

    @Override
    public Intent getServiceReportIntent(Activity activity) {
        return new Intent(activity, ServiceActivity.class);
    }

    @Override
    public String childNavigationMenuCountString() {
        return null;
    }

    @Override
    public Intent getHIA2ReportActivityIntent(Activity activity) {
        return new Intent(activity, HfHIA2ReportsActivity.class);
    }
}
