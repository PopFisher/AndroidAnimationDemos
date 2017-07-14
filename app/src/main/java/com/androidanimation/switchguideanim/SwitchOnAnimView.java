package com.androidanimation.switchguideanim;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidanimation.R;
import com.androidanimation.animations.BaseAnimatorListener;
import com.androidanimation.utils.ViewUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by popfisher on 2016/9/3.
 */

public class SwitchOnAnimView extends FrameLayout {

    private Handler mHandler = new Handler();
    /** 开关中间的圆圈View */
    private ImageView mCirclePtImgv;
    /** 手指View */
    private ImageView mFingerImgv;
    /** 手指移动的距离 */
    private float mFingerMoveDistance;
    /** 开关中间的圆圈View需要移动的距离 */
    private float mCirclePtMoveDistance;
    private static final int FINGER_ANIM_DURATION = 300;
    private static final int CIRCLE_PT_ANIM_DURATION = 500;

    private boolean isStopAnim = false;

    public SwitchOnAnimView(Context context) {
        this(context, null);
    }

    public SwitchOnAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.finger_switch_on_guide_layout, this, true);
        initView();
    }

    private void initView() {
        mCirclePtImgv = (ImageView) findViewById(R.id.switch_anim_circle_point);
        mFingerImgv = (ImageView) findViewById(R.id.finger_switch);

        // 下面两个距离要根据UI布局来确定
        mFingerMoveDistance = ViewUtil.dp2px(getContext(), 20f);
        mCirclePtMoveDistance = ViewUtil.dp2px(getContext(), 17.5f);
    }

    /**
     * 启动动画
     */
    public void startAnim() {
        isStopAnim = false;
        // 启动动画之前先恢复初始状态
        ViewHelper.setTranslationX(mCirclePtImgv, 0);
        mCirclePtImgv.setBackgroundResource(R.drawable.switch_off_circle_point);
        mFingerImgv.setBackgroundResource(R.drawable.finger_normal);
        startFingerUpAnim();
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        isStopAnim = true;
    }

    /**
     * 中间的圈点View平移动画
     */
    private void startCirclePointAnim() {
        if (mCirclePtImgv == null) {
            return;
        }
        ObjectAnimator circlePtAnim = ObjectAnimator.ofFloat(mCirclePtImgv, "translationX", 0, mCirclePtMoveDistance);
        circlePtAnim.setDuration(CIRCLE_PT_ANIM_DURATION);
        circlePtAnim.start();
    }

    /**
     * 手指向上移动动画
     */
    private void startFingerUpAnim() {
        ObjectAnimator fingerUpAnim = ObjectAnimator.ofFloat(mFingerImgv, "translationY", 0, -mFingerMoveDistance);
        fingerUpAnim.setDuration(FINGER_ANIM_DURATION);
        fingerUpAnim.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (mFingerImgv == null || mHandler == null) {
                    return;
                }
                // 手指向上动画执行完成就设置手指View背景为点击状态的背景
                mFingerImgv.setBackgroundResource(R.drawable.finger_click);
                // 点击之后为了提现停顿一下的感觉，延迟200毫秒执行其他动画
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCirclePtImgv == null || mHandler == null) {
                            return;
                        }
                        // 将中间圆圈View背景设置为开关打开状态然后开始向右平移
                        mCirclePtImgv.setBackgroundResource(R.drawable.switch_on_circle_point);
                        startCirclePointAnim();
                        // 延迟100毫秒启动手指向下平移动画
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 手指向下移动开始时设置手指背景为正常的状态
                                if (mFingerImgv != null) {
                                    mFingerImgv.setBackgroundResource(R.drawable.finger_normal);
                                }
                                startFingerDownAnim();
                            }
                        }, 100);
                    }
                }, 200);
            }
        });
        fingerUpAnim.start();
    }

    /**
     * 手指向下移动动画
     */
    private void startFingerDownAnim() {
        if (mFingerImgv == null) {
            return;
        }
        ObjectAnimator fingerDownAnim = ObjectAnimator.ofFloat(mFingerImgv, "translationY", -mFingerMoveDistance, 0);
        fingerDownAnim.setDuration(FINGER_ANIM_DURATION);
        fingerDownAnim.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // 手指向下移动动画完成，整个动画流程结束，重新开始下一次流程，循环执行动画，间隔1秒
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isStopAnim) {
                            return;
                        }
                        startAnim();
                    }
                }, 1000);
            }
        });
        fingerDownAnim.start();
    }
}
