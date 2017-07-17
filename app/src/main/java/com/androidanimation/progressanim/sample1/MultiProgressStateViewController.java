package com.androidanimation.progressanim.sample1;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;
/**
 * Created by yzs on 2017/7/14.
 * 圆形进度控件总逻辑控制器，包括状态切换，进度动画逻辑控制
 */

@TargetApi(11)
public class MultiProgressStateViewController {

    private static final int STATE_CHANGE_ANIM_DURATION = 500;
    private MultiCircleProgressView mProgressStateView;
    private ImageView mSuccessStateView;
    private ImageView mFailStateView;

    /** 传递进来的变量是成功还是失败 */
    private boolean mIsSuccess = false;
    /** 是否需要进度，蒙层上需要，Activity里面只需要结果 */
    private boolean mIsNeedProgress = true;
    private ProgressController mProgressController;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    /**
     * View的切换状态接口
     */
    public interface IViewStateChangeListener {
        /** 结果状态View动画显示完成 */
        void onResultViewShowFinish(boolean isSuccess);
    }

    private IViewStateChangeListener mViewStateChangeListener;
    public void setViewStateChangeListener(IViewStateChangeListener listener) {
        mViewStateChangeListener = listener;
    }

    public MultiProgressStateViewController(MultiProcessStateView multiProcessStateView) {
        this(multiProcessStateView, true);
    }

    public MultiProgressStateViewController(MultiProcessStateView multiProcessStateView, boolean isNeedProgress) {
        mProgressStateView = multiProcessStateView.getProgressStateView();
        mSuccessStateView = multiProcessStateView.getSuccessStateView();
        mFailStateView = multiProcessStateView.getFailStateView();
        mIsNeedProgress = isNeedProgress;
        if (isNeedProgress) {
            mProgressController = new ProgressController(mProgressStateView);
            mProgressStateView.addProgressStateListener(mProgressStateChangeListener);
        } else {
            mProgressStateView.setVisibility(View.GONE);
        }
    }

    private MultiCircleProgressView.IProgressStateChangeListener
            mProgressStateChangeListener = new MultiCircleProgressView.IProgressStateChangeListener() {
        @Override
        public void onFinished() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startChangeStateAnim();
                }
            }, 1000);
        }
    };

    private void startChangeStateAnim() {
        hideProgressView(new Runnable() {
            @Override
            public void run() {
                showResultStateView();
            }
        });
    }

    private void hideProgressView(final Runnable finishRunnable) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                ViewHelper.setAlpha(mProgressStateView, alpha);
            }
        });
        valueAnimator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (finishRunnable != null) {
                    finishRunnable.run();
                }
            }
        });
        valueAnimator.setDuration(STATE_CHANGE_ANIM_DURATION);
        valueAnimator.start();
    }

    private void showResultStateView() {
        final View targetView = mIsSuccess ? mSuccessStateView : mFailStateView;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                ViewHelper.setAlpha(targetView, alpha);
            }
        });
        valueAnimator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (mViewStateChangeListener != null) {
                    mViewStateChangeListener.onResultViewShowFinish(mIsSuccess);
                }
            }
            @Override
            public void onAnimationStart(Animator animator) {
                targetView.setVisibility(View.VISIBLE);
            }
        });
        valueAnimator.setDuration(STATE_CHANGE_ANIM_DURATION);
        valueAnimator.start();
    }

    public void start() {
        if (mProgressController != null) {
            mProgressController.autoStartAnim();
        }
    }

    public void complete(boolean success) {
        complete(success, null);
    }

    public void complete(boolean success, IViewStateChangeListener listener) {
        setViewStateChangeListener(listener);
        mIsSuccess = success;
        if (mIsNeedProgress) {
            mProgressStateView.completeQuickly();
//            mProgressController.completeProgress();
        } else {
            mSuccessStateView.setVisibility(mIsSuccess ? View.VISIBLE : View.GONE);
            mFailStateView.setVisibility(mIsSuccess ? View.GONE : View.VISIBLE);
            if (listener != null) {
                listener.onResultViewShowFinish(mIsSuccess);
            }
        }
    }

    public void onDestroy() {
        if (mProgressController != null) {
            mProgressController.onDestroy();
        }
        if (mProgressStateView != null) {
            mProgressStateView.onDestroy();
        }
    }
}
