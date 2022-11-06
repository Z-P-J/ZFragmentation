package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class LoadingDialogFragment extends CardDialogFragment<LoadingDialogFragment> {

    private TextView tvTitle;

    private String title;

    @Override
    protected boolean onBackPressed() {
        return true;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout._dialog_layout_center_impl_loading;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        implView.setBackground(DialogThemeUtils.getLoadingDialogBackground(context));
        cancelable = false;
        cancelableInTouchOutside = false;
        tvTitle = findViewById(R.id.tv_title);
        setup();
    }

    @Override
    protected int getMaxWidth() {
        return WRAP_CONTENT;
    }

    protected void setup() {
        if (title != null && tvTitle != null) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
            tvTitle.setTextColor(DialogThemeUtils.getLoadingTextColor(context));
        }
    }

    public LoadingDialogFragment setTitle(String title) {
        this.title = title;
        setup();
        return this;
    }

}
