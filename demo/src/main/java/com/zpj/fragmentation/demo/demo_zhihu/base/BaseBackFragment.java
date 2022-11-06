package com.zpj.fragmentation.demo.demo_zhihu.base;

import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.zpj.fragmentation.SimpleFragment;
import com.zpj.fragmentation.demo.R;

/**
 * Created by YoKeyword on 16/2/7.
 */
public abstract class BaseBackFragment extends SimpleFragment {

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
    }
}
