package com.zpj.fragmentation.demo.demo_zhihu.ui.fragment.first.child;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.fragmentation.demo.R;
import com.zpj.fragmentation.demo.demo_zhihu.base.BaseBackFragment;
import com.zpj.fragmentation.demo.demo_zhihu.entity.Article;
import com.zpj.fragmentation.demo.demo_zhihu.ui.fragment.CycleFragment;

/**
 * Created by YoKeyword on 16/6/5.
 */
public class FirstDetailFragment extends BaseBackFragment {
    private static final String ARG_ITEM = "arg_item";

    private Article mArticle;

    private Toolbar mToolbar;
    private ImageView mImgDetail;
    private TextView mTvTitle;
    private FloatingActionButton mFab;

    public static FirstDetailFragment newInstance(Article article) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, article);
        FirstDetailFragment fragment = new FirstDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticle = getArguments().getParcelable(ARG_ITEM);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zhihu_fragment_first_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mToolbar = findViewById(R.id.toolbar);
        mImgDetail = findViewById(R.id.img_detail);
        mTvTitle = findViewById(R.id.tv_content);
        mFab = findViewById(R.id.fab);

        mToolbar.setTitle("");
        initToolbarNav(mToolbar);
        mImgDetail.setImageResource(mArticle.getImgRes());
        mTvTitle.setText(mArticle.getTitle());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(CycleFragment.newInstance(1));
            }
        });
    }

}
