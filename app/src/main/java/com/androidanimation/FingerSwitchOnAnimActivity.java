package com.androidanimation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Matrix2f;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.androidanimation.animations.BaseAnimatorListener;
import com.androidanimation.animationview.SwitchOnAnimView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class FingerSwitchOnAnimActivity extends Activity {

    private Handler mHandler = new Handler();
    private SwitchOnAnimView mSwitchOnAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_switchon_anim);
        mSwitchOnAnimView = (SwitchOnAnimView) findViewById(R.id.switch_on_anim_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwitchOnAnimView.startAnim();
            }
        }, 500);
    }
}
