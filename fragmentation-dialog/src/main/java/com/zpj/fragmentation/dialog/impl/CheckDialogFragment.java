package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.widget.checkbox.ZCheckBox;

public class CheckDialogFragment extends AlertDialogFragment<CheckDialogFragment> {

    private ZCheckBox checkBox;
    private TextView tvTitle;

    private String checkTitle;

    private boolean isChecked;

    protected CharSequence content;

    private ZCheckBox.OnCheckedChangeListener onCheckedChangeListener;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_check;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        checkBox = findViewById(R.id.check_box);
        checkBox.setCheckedColor(DialogThemeUtils.getColorPrimary(context));
        checkBox.setChecked(isChecked);
        tvTitle = findViewById(R.id.check_title);
        tvTitle.setTextColor(DialogThemeUtils.getMajorTextColor(context));
        tvTitle.setText(checkTitle);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

        LinearLayout checkLayout = findViewById(R.id.layout_check);
        checkLayout.setOnClickListener(v -> checkBox.performClick());
    }


    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public CheckDialogFragment setCheckTitle(String checkTitle) {
        this.checkTitle = checkTitle;
        return this;
    }

    public CheckDialogFragment setChecked(boolean checked) {
        isChecked = checked;
        return this;
    }

    public CheckDialogFragment setOnCheckedChangeListener(ZCheckBox.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
        return this;
    }

}
