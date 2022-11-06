package com.zpj.fragmentation.dialog;

import android.support.annotation.NonNull;
import android.view.View;

import com.zpj.fragmentation.dialog.base.BaseDialogFragment;

import java.util.List;

public interface IDialog {

    interface OnDismissListener<T> {
        void onDismiss(T dialog);
    }

    interface OnCancelListener<T> {
        void onCancel(T dialog);
    }

    /** The identifier for the positive button. */
    int BUTTON_POSITIVE = -1;

    /** The identifier for the negative button. */
    int BUTTON_NEGATIVE = -2;

    /** The identifier for the neutral button. */
    int BUTTON_NEUTRAL = -3;

    interface OnButtonClickListener<T extends BaseDialogFragment<T>> {
        void onClick(@NonNull T fragment, int which);
    }

    interface ViewBinder<V extends View, T> {
        void onBindView(V view, T item, int position);
    }

    interface OnViewCreateListener<T> {
        void onViewCreate(@NonNull T fragment, View view);
    }

    interface OnMultiSelectListener<T, S> {
        void onSelect(S dialog, List<Integer> selected, List<T> list);
    }

    interface OnSingleSelectListener<T, S> {
        void onSelect(S dialog, int position, T item);
    }

}
