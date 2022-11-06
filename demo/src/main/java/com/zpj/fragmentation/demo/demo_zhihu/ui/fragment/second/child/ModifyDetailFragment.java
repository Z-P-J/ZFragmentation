package com.zpj.fragmentation.demo.demo_zhihu.ui.fragment.second.child;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zpj.fragmentation.demo.R;
import com.zpj.fragmentation.demo.demo_zhihu.base.BaseBackFragment;
import com.zpj.fragmentation.demo.demo_zhihu.ui.fragment.CycleFragment;

/**
 * Created by YoKeyword on 16/2/7.
 */
public class ModifyDetailFragment extends BaseBackFragment {
    private static final String ARG_TITLE = "arg_title";

    private Toolbar mToolbar;
    private EditText mEtModiyTitle;
    private Button mBtnModify, mBtnNext;

    private String mTitle;

    public static ModifyDetailFragment newInstance(String title) {
        Bundle args = new Bundle();
        ModifyDetailFragment fragment = new ModifyDetailFragment();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString(ARG_TITLE);
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        hideSoftInput();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mToolbar = view.findViewById(R.id.toolbar);
        mEtModiyTitle = view.findViewById(R.id.et_modify_title);
        mBtnModify = view.findViewById(R.id.btn_modify);
        mBtnNext = view.findViewById(R.id.btn_next);

        mToolbar.setTitle(R.string.start_result_test);
        initToolbarNav(mToolbar);

        mEtModiyTitle.setText(mTitle);

        // 显示 软键盘
//        showSoftInput(mEtModiyTitle);

        mBtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(DetailFragment.KEY_RESULT_TITLE, mEtModiyTitle.getText().toString());
                setFragmentResult(RESULT_OK, bundle);

                Toast.makeText(_mActivity, R.string.modify_success, Toast.LENGTH_SHORT).show();
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(CycleFragment.newInstance(1));
            }
        });
    }
}
