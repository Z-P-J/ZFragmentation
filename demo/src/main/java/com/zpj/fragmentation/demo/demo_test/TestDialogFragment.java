package com.zpj.fragmentation.demo.demo_test;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.zpj.fragmentation.demo.R;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;

public class TestDialogFragment extends CardDialogFragment<TestDialogFragment> {

    @Override
    protected int getImplLayoutId() {
        return R.layout.test_fragment_dialog;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "TestDialogFragment", Toast.LENGTH_SHORT).show();
                start(new MainFragment());
            }
        });
    }

}
