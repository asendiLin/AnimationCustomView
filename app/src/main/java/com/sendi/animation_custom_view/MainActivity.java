package com.sendi.animation_custom_view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sendi.animation_custom_view.button.RecordButtonActivity;
import com.sendi.animation_custom_view.doubleprogressring.DoubleProgressRingActivity;
import com.sendi.animation_custom_view.point.ManyPointViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toDoubleProgressRing(View view) {
        startActivity(new Intent(this,DoubleProgressRingActivity.class));
    }

    public void toManyPointView(View view) {
        startActivity(new Intent(this,ManyPointViewActivity.class));
    }


    public void toRecordButton(View view) {
        startActivity(new Intent(this, RecordButtonActivity.class));
    }
}
