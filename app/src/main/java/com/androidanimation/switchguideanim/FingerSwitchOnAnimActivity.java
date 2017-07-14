package com.androidanimation.switchguideanim;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.androidanimation.R;

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

    @Override
    protected void onPause() {
        super.onPause();
        mSwitchOnAnimView.stopAnim();
    }
}
