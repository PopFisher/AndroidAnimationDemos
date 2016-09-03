package com.androidanimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AnimationMainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_main);
        findViewById(R.id.finger_switch_on_anim_activity).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.finger_switch_on_anim_activity:
                Intent intent = new Intent(AnimationMainActivity.this, FingerSwitchOnAnimActivity.class);
                startActivity(intent);
                break;
        }
    }
}
