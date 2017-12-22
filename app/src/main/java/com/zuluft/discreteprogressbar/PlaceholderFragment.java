package com.zuluft.discreteprogressbar;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlaceholderFragment
        extends
        Fragment {

    public static final String KEY_PAGE = "KEY_PAGE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_placeholder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.tvText);
        textView.setText(String.valueOf(getArguments().getInt(KEY_PAGE)));
    }

    public static PlaceholderFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(KEY_PAGE, page);
        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
