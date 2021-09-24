package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.fragmentation.dialog.widget.CustomScrollView;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

public class AlertDialogFragment<T extends AlertDialogFragment<T>> extends ActionDialogFragment<T>
        implements View.OnClickListener {

    private View contentView;
    protected CharSequence content;

    @Override
    protected final int getContentLayoutId() {
        return R.layout._dialog_layout_alert;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        FrameLayout flContent = findViewById(R.id._fl_content);
        if (contentView == null && !TextUtils.isEmpty(content)) {
            this.contentView = createContentView(content);
        }

        if (contentView != null) {
            flContent.addView(contentView);
        }

        View shadowBottomView = findViewById(R.id.view_shadow_bottom);
        View shadowUpView = findViewById(R.id.view_shadow_up);

        if (shadowBottomView != null && shadowUpView != null) {
            CustomScrollView scrollView = findViewById(R.id._scroll_view);
            scrollView.setOnScrollChangeListener(new CustomScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollToStart() {

                }

                @Override
                public void onScrollToEnd() {

                }

                @Override
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    shadowBottomView.setVisibility(scrollView.isScrollToTop() ? View.GONE : View.VISIBLE);
                    shadowUpView.setVisibility(scrollView.isScrollToBottom() ? View.GONE : View.VISIBLE);
                }
            });

            postOnEnterAnimationEnd(new Runnable() {
                @Override
                public void run() {
                    shadowBottomView.setVisibility(scrollView.isScrollToTop() ? View.GONE : View.VISIBLE);
                    shadowUpView.setVisibility(scrollView.isScrollToBottom() ? View.GONE : View.VISIBLE);
                }
            });
        }
    }

    protected View createContentView(CharSequence content) {
        TextView textView = new TextView(context);
        textView.setText(content);
        textView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
        textView.setTextSize(14);
        int padding = ScreenUtils.dp2pxInt(context, 24);
        textView.setPadding(padding, padding / 3, padding, padding / 3);
        textView.setMinLines(3);
        textView.setLineSpacing(6, 1);
        return textView;
    }

    public T setContent(CharSequence content) {
        this.content = content;
        return self();
    }

    public T setContent(@StringRes int resId) {
        return setContent(ContextUtils.getApplicationContext().getResources().getString(resId));
    }

    public T setContent(@LayoutRes int resId, IDialog.OnViewCreateListener<T> listener) {
        this.contentView = LayoutInflater.from(ContextUtils.getApplicationContext()).inflate(resId, null, false);
        this.onViewCreateListener = listener;
        return self();
    }

    public T setContent(View view) {
        this.contentView = view;
        return self();
    }

}
