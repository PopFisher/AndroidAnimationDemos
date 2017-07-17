package com.androidanimation.progressanim;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.androidanimation.R;
import com.androidanimation.progressanim.sample1.MultiProcessStateView;
import com.androidanimation.progressanim.sample1.MultiProgressStateViewController;

public class ProgressActivity extends Activity implements View.OnClickListener {

    private MultiProcessStateView mMultiProcessStateView;
    private MultiProgressStateViewController mMultiProgressStateViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_anim_layout);
        mMultiProcessStateView = (MultiProcessStateView) findViewById(R.id.multi_progress_state_view);
        mMultiProgressStateViewController = new MultiProgressStateViewController(mMultiProcessStateView);
        findViewById(R.id.multi_progress_complete_success).setOnClickListener(this);
        findViewById(R.id.multi_progress_complete_fail).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mMultiProgressStateViewController.start();
    }

    /**
     * 测试surfaceView在WindowManager中的情况
     * 坑1：在WindowManager中执行画布裁剪失效
     */
    private void testWindowManagerSurfaceView() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        MultiProcessStateView multiProcessStateView = new MultiProcessStateView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.format = PixelFormat.TRANSPARENT;
        windowManager.addView(multiProcessStateView, params);
    }

    @Override
    protected void onDestroy() {
        mMultiProgressStateViewController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.multi_progress_complete_success:
                mMultiProgressStateViewController.complete(true);
                break;
            case R.id.multi_progress_complete_fail:
                mMultiProgressStateViewController.complete(false);
                break;
        }
    }
}
