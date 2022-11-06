package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.checkbox.ZCheckBox;

public class CheckDialogFragment extends AlertDialogFragment<CheckDialogFragment> {

    protected ZCheckBox checkBox;
    protected TextView tvTitle;

    protected String checkTitle;
    protected int mTitleColor;
    protected int mTitleSize = 14;

    protected boolean isChecked;

    protected ZCheckBox.OnCheckedChangeListener onCheckedChangeListener;

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        LinearLayout actionContainer = findViewById(R.id._ll_container);
        LinearLayout checkLayout = new LinearLayout(context);
        checkLayout.setGravity(Gravity.CENTER_VERTICAL);
        int dp24 = ScreenUtils.dp2pxInt(24);
        int dp4 = ScreenUtils.dp2pxInt(8);
        checkLayout.setPadding(dp24, ScreenUtils.dp2pxInt(12), dp24, dp4);
        checkLayout.setOnClickListener(v -> checkBox.performClick());

        actionContainer.addView(checkLayout, actionContainer.indexOfChild(mContentView) + 1,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        checkBox = new ZCheckBox(context);
        checkBox.setBorderSize(ScreenUtils.dp2px(2));
        checkBox.setCheckedColor(DialogThemeUtils.getColorPrimary(context));
        checkBox.setChecked(isChecked);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        int size = ScreenUtils.dp2pxInt(20);
        checkLayout.addView(checkBox, new ViewGroup.LayoutParams(size, size));


        tvTitle = new TextView(context);
        if (mTitleColor == 0) {
            mTitleColor = DialogThemeUtils.getMajorTextColor(context);
        }
        tvTitle.setText(checkTitle);
        tvTitle.setTextColor(mTitleColor);
        tvTitle.setTextSize(mTitleSize);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp4 * 2;
        checkLayout.addView(tvTitle, params);
    }

    @Override
    protected View createContentView(CharSequence content) {
        TextView textView = new TextView(context);
        textView.setText(content);
        textView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
        textView.setTextSize(15);
        int padding = ScreenUtils.dp2pxInt(context, 24);
        textView.setPadding(padding, 0, padding, padding / 3);
        textView.setMinLines(2);
        textView.setLineSpacing(6, 1);
        return textView;
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public CheckDialogFragment setCheckTitle(String checkTitle) {
        this.checkTitle = checkTitle;
        return this;
    }

    public CheckDialogFragment setCheckTitleColor(int color) {
        this.mTitleColor = color;
        return this;
    }

    public CheckDialogFragment setCheckTitleSize(int size) {
        this.mTitleSize = size;
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
