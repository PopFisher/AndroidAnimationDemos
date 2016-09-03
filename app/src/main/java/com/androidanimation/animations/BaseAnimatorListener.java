package com.androidanimation.animations;

import com.nineoldandroids.animation.Animator;

/**
 * 基类实现Animator.AnimatorListener接口，子类根据需要选择性使用
 */

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
