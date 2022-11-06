package com.zpj.fragmentation.dialog.base;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.DialogAnimator;
import com.zpj.fragmentation.dialog.animator.ShadowMaskAnimator;
import com.zpj.fragmentation.dialog.animator.TranslateAnimator;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;
import com.zpj.fragmentation.dialog.enums.DialogPosition;
import com.zpj.fragmentation.dialog.interfaces.OnClickOutsideListener;
import com.zpj.fragmentation.dialog.widget.PartShadowContainer;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class PartShadowDialogFragment<T extends PartShadowDialogFragment<T>> extends AttachDialogFragment<T> {

    private static final String TAG = "PartShadowDialog";

    protected PartShadowContainer attachPopupContainer;
    protected View contentView;

    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_attach_view;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (attachView == null && touchPoint == null) {
            dismiss();
            return;
        }
        super.initView(view, savedInstanceState);
        getImplView().setAlpha(0f);

        attachPopupContainer = findViewById(R.id.attachPopupContainer);

        contentView = getLayoutInflater().inflate(getContentLayoutId(), attachPopupContainer, false);
        attachPopupContainer.addView(contentView);
        if (bgDrawable != null) {
            contentView.setBackground(bgDrawable);
        }
    }

    @Override
    protected DialogAnimator onCreateShadowAnimator(FrameLayout flContainer) {
        return new ShadowMaskAnimator(getImplView());
    }

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup implView) {
        //1. apply width and height
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getImplView().getLayoutParams();
        if (rotation == 0) {
            params.width = getImplView().getMeasuredWidth(); // 满宽
        } else if (rotation == 1 || rotation == 3) {
            params.width = getImplView().getMeasuredWidth() - (StatusBarUtils.isNavBarVisible(context) ? ScreenUtils.getNavBarHeight(context) : 0);
        }

        Log.d(TAG, "rotation=" + rotation);

        //1. 获取atView在屏幕上的位置
        int[] locations = new int[2];
        attachView.getLocationOnScreen(locations);
        Rect rect = new Rect(locations[0], locations[1], locations[0] + attachView.getMeasuredWidth(),
                locations[1] + attachView.getMeasuredHeight());
        int centerY = rect.top + rect.height() / 2;

//        int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
        int[] rootLocations = new int[2];
        getRootView().getLocationOnScreen(rootLocations);
        int offset = rootLocations[1];

        if ((centerY > getImplView().getMeasuredHeight() || dialogPosition == DialogPosition.Top) && dialogPosition != DialogPosition.Bottom) {
            // 说明atView在Window下半部分，PartShadow应该显示在它上方，计算atView之上的高度
            params.height = rect.top - offset;
            params.width = MATCH_PARENT;
            isShowUp = true;
            params.topMargin = -mOffsetY;
            // 同时自定义的impl View应该Gravity居于底部

            FrameLayout.LayoutParams implParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
            implParams.gravity = Gravity.BOTTOM;
//            if(getMaxHeight()!=0)
//                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implParams.width = MATCH_PARENT;
            implParams.height = WRAP_CONTENT;
            contentView.setLayoutParams(implParams);

        } else {
            // atView在上半部分，PartShadow应该显示在它下方，计算atView之下的高度
//            params.height = getContentView().getMeasuredHeight() - rect.bottom;
            params.height = getRootView().getMeasuredHeight() - rect.bottom + offset;
//            params.height = ScreenUtils.getScreenHeight(context) - rect.bottom;
            params.width = MATCH_PARENT;
            // 防止伸到导航栏下面
//            if (XPopupUtils.isNavBarVisible(getContext())) {
//                params.height -= XPopupUtils.getNavBarHeight();
//            }
            isShowUp = false;
            params.topMargin = rect.bottom + mOffsetY - offset;

            // 同时自定义的impl View应该Gravity居于顶部

            FrameLayout.LayoutParams implParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
            implParams.gravity = Gravity.TOP;
//            if(getMaxHeight()!=0)
//                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implParams.width = MATCH_PARENT;
            implParams.height = WRAP_CONTENT;
            contentView.setLayoutParams(implParams);
        }
        getImplView().setLayoutParams(params);

        attachPopupContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (cancelableInTouchOutside) dismiss();
                return false;
            }
        });
        attachPopupContainer.setOnClickOutsideListener(new OnClickOutsideListener() {
            @Override
            public void onClickOutside() {
                if (cancelableInTouchOutside) dismiss();
            }
        });

        return new TranslateAnimator(contentView, isShowUp ?
                DialogAnimation.TranslateFromBottom : DialogAnimation.TranslateFromTop);
    }

    protected View getContentView() {
        return contentView;
    }

    //    //让触摸透过
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(cancelableInTouchOutside){
//            dismiss();
//        }
//        return !cancelableInTouchOutside;
//    }

}
