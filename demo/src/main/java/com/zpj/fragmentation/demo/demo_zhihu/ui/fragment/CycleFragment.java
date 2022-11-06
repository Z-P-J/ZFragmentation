package com.zpj.fragmentation.demo.demo_zhihu.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zpj.fragmentation.demo.R;
import com.zpj.fragmentation.demo.demo_zhihu.base.BaseBackFragment;

/**
 * Created by YoKeyword on 16/2/7.
 */
public class CycleFragment extends BaseBackFragment {
    private static final String ARG_NUMBER = "arg_number";

    private Toolbar mToolbar;
    private TextView mTvName;
    private Button mBtnNext, mBtnNextWithFinish;

    private int mNumber;

    public static CycleFragment newInstance(int number) {
        CycleFragment fragment = new CycleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mNumber = args.getInt(ARG_NUMBER);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cycle;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mToolbar = findViewById(R.id.toolbar);
        mTvName = findViewById(R.id.tv_name);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnNextWithFinish = findViewById(R.id.btn_next_with_finish);

        String title = "CyclerFragment " + mNumber;

        mToolbar.setTitle(title);
        initToolbarNav(mToolbar);

        mTvName.setText(title);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(CycleFragment.newInstance(mNumber + 1));
            }
        });
        mBtnNextWithFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithPop(CycleFragment.newInstance(mNumber + 1));
            }
        });
    }


}
