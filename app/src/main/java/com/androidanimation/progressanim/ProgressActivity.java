package com.androidanimation.progressanim;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.androidanimation.R;

public class ProgressActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_anim_layout);
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.finger_switch_on_anim_activity:
                break;
        }
    }
}
