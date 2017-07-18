package com.androidanimation.progressanim.sample2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;

import com.androidanimation.progressanim.BaseAnimatorListener;

/**
 * Created by popfisher on 2017/7/14.
 * 这个运行在UI线程，所以在没有性能瓶颈的情况下比较好控制进度的变化，运行在UI线程会掉帧导致动画不流畅
 */

@TargetApi(11)
public class ProgressController {

    private static final int FIRST_DURATION = 10000;            // 10秒
    private static final int SECOND_DURATION = 20000;         // 20秒
    private static final int LAST_DURATION = 1000;         // 1秒
    private static final float FIRST_END_ANGLE = 180f;
    private static final float SECOND_END_ANGLE = 360f;
    private static final float LAST_END_ANGLE = 360f;

    /** 是否需要快速完成剩下的进度 */
    private boolean isCompleteProgress = false;
    private MultiCircleProgressNormalView mProgressStateView;
    private ValueAnimator mFirstValueAnimator;
    private ValueAnimator mSecondValueAnimator;
    private ValueAnimator mCompleteValueAnimator;

    public ProgressController(MultiCircleProgressNormalView progressStateView) {
        mProgressStateView = progressStateView;
    }

    public void autoStartAnim() {
        isCompleteProgress = false;
        startFirstAnim();
    }

    public void completeProgress() {
        isCompleteProgress = true;
        completeAnim();
    }

    private void startFirstAnim() {
        mFirstValueAnimator = ValueAnimator.ofFloat(0, FIRST_END_ANGLE);
        mFirstValueAnimator.setDuration(FIRST_DURATION);
        mFirstValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isCompleteProgress) {
                    return;
                }
                float angle = (float) animation.getAnimatedValue();
                mProgressStateView.setAngle(angle);
            }
        });
        mFirstValueAnimator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                startSecondAnim();
            }
        });
        mFirstValueAnimator.start();
    }

    private void startSecondAnim() {
        mSecondValueAnimator = ValueAnimator.ofFloat(mProgressStateView.getAngle(), SECOND_END_ANGLE);
        mSecondValueAnimator.setDuration(SECOND_DURATION);
        mSecondValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isCompleteProgress) {
                    return;
                }
                float angle = (float) animation.getAnimatedValue();
                mProgressStateView.setAngle(angle);
            }
        });
        mSecondValueAnimator.start();
    }

    private void completeAnim() {
        mFirstValueAnimator.cancel();
        mSecondValueAnimator.cancel();

        mCompleteValueAnimator = ValueAnimator.ofFloat(mProgressStateView.getAngle(), LAST_END_ANGLE);
        mCompleteValueAnimator.setDuration(LAST_DURATION);
        mCompleteValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                mProgressStateView.setAngle(angle);
            }
        });
        mCompleteValueAnimator.start();
    }

    public void onDestroy() {
        mFirstValueAnimator = null;
        mSecondValueAnimator = null;
        mCompleteValueAnimator = null;
    }
}
