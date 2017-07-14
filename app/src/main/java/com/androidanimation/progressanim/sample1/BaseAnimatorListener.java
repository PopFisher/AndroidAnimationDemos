package com.androidanimation.progressanim.sample1;

import android.animation.Animator;
import android.annotation.TargetApi;

/**
 * 基类实现Animator.AnimatorListener接口，子类根据需要选择性使用
 */

@TargetApi(11)
public class BaseAnimatorListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
