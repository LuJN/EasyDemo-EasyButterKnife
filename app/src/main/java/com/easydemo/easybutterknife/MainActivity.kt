package com.easydemo.easybutterknife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.easydemo.butterknife.ButterKnife
import com.easydemo.butterknife_annotations.BindView
import com.easydemo.butterknife_runtime.Unbinder

open class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @BindView(R.id.textView)
    @JvmField
    var textView: TextView? = null

    private var unBinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        unBinder = ButterKnife.bind(this)
        Log.d(TAG, "onCreate: " + (textView == null))
        textView?.setOnClickListener {
            startActivity(Intent(this, MainExtendActivity::class.java))
        }
    }

    override fun onDestroy() {
        unBinder?.unbind()
        val bindingClass = classLoader.loadClass("com.easydemo.easybutterknife.MainActivityBinding")
        val activityField = bindingClass.getDeclaredField("activity")
        activityField.isAccessible = true
        Log.d(TAG, "onDestroy: " + (activityField.get(unBinder)))
        super.onDestroy()
    }
}