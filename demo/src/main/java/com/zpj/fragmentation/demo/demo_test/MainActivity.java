package com.zpj.fragmentation.demo.demo_test;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.fragmentation.demo.R;

/**
 * 类知乎 复杂嵌套Demo tip: 多使用右上角的"查看栈视图"
 * Created by YoKeyword on 16/6/2.
 */
public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_main);
        MainFragment fragment = findFragment(MainFragment.class);
        if (fragment == null) {
            fragment = new MainFragment();
            loadRootFragment(R.id.fl_container, fragment);
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
//        return new DefaultNoAnimator();
    }
}
