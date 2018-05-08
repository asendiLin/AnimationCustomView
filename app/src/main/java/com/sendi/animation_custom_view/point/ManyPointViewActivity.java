package com.sendi.animation_custom_view.point;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sendi.animation_custom_view.R;
import com.sendi.animation_custom_view.point.ManyPointView;

public class ManyPointViewActivity extends AppCompatActivity {

    ManyPointView mManyPointView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_many_point_view);
        mManyPointView=findViewById(R.id.many_point_view);


    }

    public void start(View view) {
        mManyPointView.startAnim();
    }
}
