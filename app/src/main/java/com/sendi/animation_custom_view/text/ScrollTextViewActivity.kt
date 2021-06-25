package com.sendi.animation_custom_view.text

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.sendi.animation_custom_view.R

class ScrollTextViewActivity : AppCompatActivity() {

    private lateinit var mScrollTextView: ScrollTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_text_view)
        mScrollTextView = findViewById(R.id.scroll_text_view)
        mScrollTextView.setNumber(0, false)
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            mScrollTextView.setNumber(8, true)
        }
    }
}
