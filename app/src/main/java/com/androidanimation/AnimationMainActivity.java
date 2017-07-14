package com.androidanimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidanimation.progressanim.ProgressActivity;
import com.androidanimation.switchguideanim.FingerSwitchOnAnimActivity;

public class AnimationMainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_main);
        findViewById(R.id.finger_switch_on_anim_activity).setOnClickListener(this);
        findViewById(R.id.progress_anim_activity).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.finger_switch_on_anim_activity:
                startActivity(new Intent(AnimationMainActivity.this, FingerSwitchOnAnimActivity.class));
                break;
            case R.id.progress_anim_activity:
                startActivity(new Intent(AnimationMainActivity.this, ProgressActivity.class));
                break;
        }
    }
}
