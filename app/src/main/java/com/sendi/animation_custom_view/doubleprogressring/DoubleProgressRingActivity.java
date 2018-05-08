package com.sendi.animation_custom_view.doubleprogressring;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sendi.animation_custom_view.R;

public class DoubleProgressRingActivity extends AppCompatActivity {

    private DoubleProgressRing mDoubleProgressRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_progress_ring);
        mDoubleProgressRing=findViewById(R.id.double_progress_ring);
        mDoubleProgressRing.startAni();//开始做动画
    }
}
