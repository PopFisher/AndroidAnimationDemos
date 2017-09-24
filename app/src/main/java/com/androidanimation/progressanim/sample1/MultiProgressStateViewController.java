package com.androidanimation.progressanim.sample1;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.androidanimation.progressanim.BaseAnimatorListener;
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
    private Handler mHandler = new Handler(Looper.getMainLooper());


    /**
     * View的切换状态接口
     */
    public interface IViewStateChangeListener {
        /** 结果状态View动画显示完成 */
        void onResultViewShowFinish(boolean isSuccess);
        /** 进度执行到100%时回调 */
        void onProgressFinish();
    }

    /**
     * 外部指定快速滚动到指定的进度
     */
    public interface ISmoothScrollListener {
        /** 进度执行到外部指定的值时回调 */
        void onSmoothScrollFinish();
    }

    private IViewStateChangeListener mViewStateChangeListener;
    public void setViewStateChangeListener(IViewStateChangeListener listener) {
        mViewStateChangeListener = listener;
    }
    private ISmoothScrollListener mSmoothScrollListener;
    public void setSmoothScrollListener(ISmoothScrollListener listener) {
        mSmoothScrollListener = listener;
    }

    public MultiProgressStateViewController(MultiProcessStateView multiProcessStateView) {
        mProgressStateView = multiProcessStateView.getProgressStateView();
        mSuccessStateView = multiProcessStateView.getSuccessStateView();
        mFailStateView = multiProcessStateView.getFailStateView();
        mProgressStateView.addProgressStateListener(mProgressStateChangeListener);
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

        @Override
        public void onSmoothScrollFinish() {
            if (mSmoothScrollListener != null) {
                mSmoothScrollListener.onSmoothScrollFinish();
            }
        }
    };

    private void startChangeStateAnim() {
        hideProgressView(new Runnable() {
            @Override
            public void run() {
                mProgressStateView.stop();
                mProgressStateView.setVisibility(View.GONE);
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

    public void complete(boolean success) {
        complete(success, null);
    }

    public void complete(boolean success, IViewStateChangeListener listener) {
        setViewStateChangeListener(listener);
        mIsSuccess = success;
        mProgressStateView.completeQuickly();
    }

    public void smoothScrollToProgress(int progress, ISmoothScrollListener listener) {
        setSmoothScrollListener(listener);
        mProgressStateView.smoothScrollToProgress(progress);
    }

    public void reStart() {
        mProgressStateView.stop();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setAlpha(mProgressStateView, 1.0f);
                mProgressStateView.setVisibility(View.VISIBLE);
                mSuccessStateView.setVisibility(View.GONE);
                mFailStateView.setVisibility(View.GONE);
                mProgressStateView.start();
            }
        }, STATE_CHANGE_ANIM_DURATION);
    }

    public void onDestroy() {
        if (mProgressStateView != null) {
            mProgressStateView.onDestroy();
        }
    }
}
