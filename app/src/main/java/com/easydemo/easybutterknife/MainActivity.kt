package com.easydemo.easybutterknife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.easydemo.butterknife.ButterKnife
import com.easydemo.butterknife_annotations.BindView

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @BindView(R.id.textView)
    @JvmField
    var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        Log.d(TAG, "onCreate: " + (textView == null))
    }
}