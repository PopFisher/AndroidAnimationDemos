package com.androidanimation.progressanim;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.androidanimation.R;
import com.androidanimation.progressanim.sample1.MultiProcessStateView;
import com.androidanimation.progressanim.sample1.MultiProgressStateViewController;

public class ProgressActivity extends Activity implements View.OnClickListener {

    private MultiProcessStateView mMultiProcessStateView;
    private MultiProgressStateViewController mMultiProgressStateViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_anim_layout);
        mMultiProcessStateView = (MultiProcessStateView) findViewById(R.id.multi_progress_state_view);
        mMultiProgressStateViewController = new MultiProgressStateViewController(mMultiProcessStateView);
        findViewById(R.id.multi_progress_complete_success).setOnClickListener(this);
        findViewById(R.id.multi_progress_complete_fail).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMultiProgressStateViewController.start();
    }

    @Override
    protected void onDestroy() {
        mMultiProgressStateViewController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.multi_progress_complete_success:
                mMultiProgressStateViewController.complete(true);
                break;
            case R.id.multi_progress_complete_fail:
                mMultiProgressStateViewController.complete(false);
                break;
        }
    }
}
