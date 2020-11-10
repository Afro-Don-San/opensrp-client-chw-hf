package org.smartregister.chw.hf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.smartregister.chw.core.fragment.DraftMonthlyFragment;
import org.smartregister.chw.hf.R;

public class HfDraftMonthlyFragment extends DraftMonthlyFragment {
    public static HfDraftMonthlyFragment newInstance() {
        HfDraftMonthlyFragment fragment = new HfDraftMonthlyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.findViewById(R.id.start_new_report_enabled).setVisibility(View.GONE);
        }
        return view;
    }
}
