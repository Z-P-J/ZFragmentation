package com.zpj.fragmentation.demo.demo_zhihu.ui.fragment.third;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;

import com.zpj.fragmentation.demo.R;
import com.zpj.fragmentation.demo.demo_zhihu.base.BaseMainFragment;
import com.zpj.fragmentation.demo.demo_zhihu.ui.fragment.third.child.ShopFragment;

/**
 * Created by YoKeyword on 16/6/3.
 */
public class ZhihuThirdFragment extends BaseMainFragment {

    public static ZhihuThirdFragment newInstance() {

        Bundle args = new Bundle();

        ZhihuThirdFragment fragment = new ZhihuThirdFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zhihu_fragment_third;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        if (findChildFragment(ShopFragment.class) == null) {
            // ShopFragment是flow包里的
            loadRootFragment(R.id.fl_third_container, ShopFragment.newInstance());
        }
    }
}
