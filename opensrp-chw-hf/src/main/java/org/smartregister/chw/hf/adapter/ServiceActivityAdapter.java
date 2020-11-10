package org.smartregister.chw.hf.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.HfHIA2ReportsActivity;
import org.smartregister.chw.hf.activity.ProvidersReportListActivity;

import java.util.List;

public class ServiceActivityAdapter extends RecyclerView.Adapter<ServiceActivityAdapter.ServiceActivityViewHolder> {
    private List<String> serviceItems;
    private Context context;
    private String providerType = CoreConstants.HfInAppUtil.PROVIDER_TYPE;

    public ServiceActivityAdapter(List<String> serviceItems, Context context) {
        this.serviceItems = serviceItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater;
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.service_activity_items, parent, false);
        return new ServiceActivityAdapter.ServiceActivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceActivityViewHolder holder, int position) {
        String Item = serviceItems.get(position);
        holder.serviceItem.setText(Item);
        holder.view.setOnClickListener(v -> {
            Intent intent = getIntent(context, Item);
            context.startActivity(intent);
        });
    }

    private Intent getIntent(Context activity, String Item) {
        if (Item.equalsIgnoreCase(context.getString(R.string.service_activity_reporting))) {
            return new Intent(activity, HfHIA2ReportsActivity.class);
        } else if (Item.equalsIgnoreCase(context.getString(R.string.review_chw_services))) {
            Intent intent = new Intent(activity, ProvidersReportListActivity.class);
            intent.putExtra(providerType, providerType);
            return intent;
        }
        return new Intent(activity, null);
    }

    @Override
    public int getItemCount() {
        return serviceItems.size();
    }

    public static class ServiceActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView serviceItem;
        private ImageView goToDetails;
        private View view;

        public ServiceActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            serviceItem = itemView.findViewById(R.id.service_activity_items);
            goToDetails = itemView.findViewById(R.id.go_to_item_details_image_view);
        }
    }
}
