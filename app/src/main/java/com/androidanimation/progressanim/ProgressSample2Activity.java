package com.androidanimation.progressanim;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidanimation.R;
import com.androidanimation.progressanim.sample2.MultiProcessStateView;
import com.androidanimation.progressanim.sample2.MultiProgressStateViewController;

public class ProgressSample2Activity extends Activity implements View.OnClickListener {

    private MultiProcessStateView mMultiProcessStateView;
    private MultiProgressStateViewController mMultiProgressStateViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_sample2_layout);
        mMultiProcessStateView = (MultiProcessStateView) findViewById(R.id.multi_progress_state_normal_view);
        mMultiProgressStateViewController = new MultiProgressStateViewController(mMultiProcessStateView);
        findViewById(R.id.multi_progress_complete_success_normal).setOnClickListener(this);
        findViewById(R.id.multi_progress_complete_fail_normal).setOnClickListener(this);
        findViewById(R.id.multi_progress_smoothscroll_normal).setOnClickListener(this);
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
            case R.id.multi_progress_complete_success_normal:
                mMultiProgressStateViewController.complete(true, new MultiProgressStateViewController.IViewStateChangeListener() {
                    @Override
                    public void onResultViewShowFinish(boolean isSuccess) {
                        Log.d("Progress", "Success onResultViewShowFinish isSuccess: " + isSuccess);
                    }

                    @Override
                    public void onProgressFinish() {
                        Log.d("Progress", "onProgressFinish");
                    }
                });
                break;
            case R.id.multi_progress_complete_fail_normal:
                mMultiProgressStateViewController.complete(false, new MultiProgressStateViewController.IViewStateChangeListener() {
                    @Override
                    public void onResultViewShowFinish(boolean isSuccess) {
                        Log.d("Progress", "Fail onResultViewShowFinish isSuccess: " + isSuccess);
                    }

                    @Override
                    public void onProgressFinish() {
                        Log.d("Progress", "onProgressFinish");
                    }
                });
                break;
            case R.id.multi_progress_smoothscroll_normal:
                mMultiProgressStateViewController.smoothScrollToProgress(80, new MultiProgressStateViewController.ISmoothScrollListener() {
                    @Override
                    public void onSmoothScrollFinish() {
                        Log.d("Progress", "onSmoothScrollFinish");
                    }
                });
                break;
        }
    }
}
