package com.sendi.animation_custom_view.progress_line

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sendi.animation_custom_view.R
import com.sendi.commentdemo.MMProgressLine

class ProgressLineActivity : AppCompatActivity() {

    var mProgressLine: MMProgressLine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_line)

        mProgressLine = findViewById(R.id.progress_line)
    }

    fun onStart(view: View) {
        mProgressLine?.startProgressAnimation(0.9f)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgressLine?.stop()
    }
}
