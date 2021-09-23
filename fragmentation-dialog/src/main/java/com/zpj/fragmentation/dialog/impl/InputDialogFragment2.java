package com.zpj.fragmentation.dialog.impl;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;

public class InputDialogFragment2 extends ActionDialogFragment<InputDialogFragment2> implements View.OnClickListener {

    private boolean autoShowKeyboard = true;
    private boolean emptyable = false;
    private int selectionStart = 0;
    private int selectionEnd = -1;
    private int minLines = 0;
    private int maxLines = Integer.MAX_VALUE;
    private boolean singleLine = true;

    private AppCompatEditText et_input;
    private CharSequence tips;
    private CharSequence inputContent;
    private CharSequence hint;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_input;
    }

    @Override
    protected void initContentView(View contentView) {

        LinearLayout actionContainer = findViewById(R.id._ll_container);
        if (!TextUtils.isEmpty(tips)) {
            actionContainer.addView(createContentView(tips), actionContainer.indexOfChild(mContentView));
        }

        et_input = findViewById(R.id.et_input);
        et_input.setVisibility(View.VISIBLE);
        et_input.setSingleLine(singleLine);
        et_input.setMinLines(minLines);
        et_input.setMaxLines(maxLines);
        et_input.getLayoutParams();
        int dp16 = ScreenUtils.dp2pxInt(context, 16);
        View centerPopupContainer = getImplView();
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, centerPopupContainer, height -> {
            if (height > 0 && isSupportVisible()) {
                Rect rect = new Rect();
                et_input.getGlobalVisibleRect(rect);
                float bottom = ScreenUtils.getScreenHeight(context) - rect.bottom - dp16;
                if (bottom < height) {
                    centerPopupContainer.setTranslationY(bottom - height);
                }
            } else {
                centerPopupContainer.setTranslationY(0);
            }
        });
        if (!TextUtils.isEmpty(hint)) {
            et_input.setHint(hint);
        }
        if (!TextUtils.isEmpty(inputContent)) {
            et_input.setText(inputContent);
            if (selectionStart < 0) {
                selectionStart = 0;
            }
            if (selectionStart > inputContent.length()) {
                selectionStart = inputContent.length();
            }
            if (selectionEnd < selectionStart || selectionEnd > inputContent.length()) {
                selectionEnd = inputContent.length();
            }
            if (selectionStart <= selectionEnd) {
                et_input.setSelection(selectionStart, selectionEnd);
            }
        }

        et_input.setTextColor(DialogThemeUtils.getMajorTextColor(context));
        et_input.setHintTextColor(DialogThemeUtils.getNormalTextColor(context));
        et_input.post(new Runnable() {
            @Override
            public void run() {
                if (autoShowKeyboard) {
                    showSoftInput(et_input);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (minLines < 0) {
            minLines = 0;
        }
        if (maxLines < minLines) {
            maxLines = minLines;
        }
    }

    @Override
    public void onDestroyView() {
        hideSoftInput();
        super.onDestroyView();
    }

    protected View createContentView(CharSequence content) {
        TextView textView = new TextView(context);
        textView.setText(content);
        textView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
        textView.setTextSize(14);
        int padding = ScreenUtils.dp2pxInt(context, 24);
        textView.setPadding(padding, padding / 3, padding, padding / 3);
        textView.setLineSpacing(6, 1);
        return textView;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (cancelListener != null) {
                cancelListener.onClick(this, IDialog.BUTTON_NEGATIVE);
            }
            if (autoDismiss) {
                dismiss();
            }
        } else if (v == tv_confirm) {
            if (!emptyable && TextUtils.isEmpty(getText())) {
                Toast.makeText(context, "输入不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (confirmListener != null) {
                confirmListener.onClick(this, IDialog.BUTTON_POSITIVE);
            }
            if (autoDismiss) {
                dismiss();
            }
        }
    }

    public InputDialogFragment2 setMinLines(int minLines) {
        this.minLines = minLines;
        return this;
    }

    public InputDialogFragment2 setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        return this;
    }

    public InputDialogFragment2 setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        return this;
    }

    public InputDialogFragment2 setEditText(CharSequence inputContent) {
        this.inputContent = inputContent;
        return this;
    }

    public InputDialogFragment2 setEditText(@StringRes int resId) {
        return setEditText(ContextUtils.getApplicationContext().getResources().getString(resId));
    }

    public InputDialogFragment2 setHint(CharSequence hint) {
        this.hint = hint;
        return this;
    }

    public InputDialogFragment2 setHint(@StringRes int resId) {
        return setHint(ContextUtils.getApplicationContext().getResources().getString(resId));
    }

    public InputDialogFragment2 setTipText(CharSequence tips) {
        this.tips = tips;
        return self();
    }

    public InputDialogFragment2 setTipText(@StringRes int resId) {
        return setTipText(ContextUtils.getApplicationContext().getResources().getString(resId));
    }

    public InputDialogFragment2 setEmptyable(boolean emptyable) {
        this.emptyable = emptyable;
        return this;
    }

    public InputDialogFragment2 setSelection(int start, int stop) {
        this.selectionStart = start;
        this.selectionEnd = stop;
        return this;
    }

    public InputDialogFragment2 setAutoShowKeyboard(boolean autoShowKeyboard) {
        this.autoShowKeyboard = autoShowKeyboard;
        return this;
    }

    public AppCompatEditText getEditText() {
        return et_input;
    }

    public String getText() {
        if (getEditText().getText() == null) {
            return "";
        }
        return getEditText().getText().toString();
    }

}
