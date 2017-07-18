package com.androidanimation.progressanim.sample2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidanimation.R;

/**
 * Created by popfisher on 2017/7/14.
 */

public class MultiProcessStateView extends FrameLayout {
    private MultiCircleProgressNormalView mProgressStateNormalView;
    private ImageView mSuccessStateView;
    private ImageView mFailStateView;

    public MultiProcessStateView(Context context) {
        this(context, null);
    }

    public MultiProcessStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.multi_process_state_normal_layout, this, true);
        mProgressStateNormalView = (MultiCircleProgressNormalView) findViewById(R.id.process_animating_state_view);
        mSuccessStateView = (ImageView) findViewById(R.id.process_finish_success_state_view);
        mFailStateView = (ImageView) findViewById(R.id.process_finish_fail_state_view);
    }

    public MultiCircleProgressNormalView getProgressStateView() {
        return mProgressStateNormalView;
    }

    public ImageView getSuccessStateView() {
        return mSuccessStateView;
    }

    public ImageView getFailStateView() {
        return mFailStateView;
    }
}
