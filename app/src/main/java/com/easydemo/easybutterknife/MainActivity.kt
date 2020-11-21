package com.easydemo.easybutterknife

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easydemo.butterknife.ButterKnife
import com.easydemo.butterknife_annotations.BindView
import com.easydemo.butterknife_annotations.OnClick
import com.easydemo.butterknife_runtime.Unbinder

open class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @BindView(R.id.textView)
    @JvmField
    var textView: TextView? = null

    private lateinit var unBinder: Unbinder

//    @OnClick(R.id.textView, R.id.textView2)
//    fun onClick() {
//        // 小米手机Toast带应用名，e.g. "EasyDemo-EasyButterKnife:@OnClick"
//        // 解决方法：需要先makeText传""，再setText
//        val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
//        toast.setText("@OnClick")
//        toast.show()
//    }

    @OnClick(R.id.textView, R.id.textView2)
    fun onClick(view: View) {
        // 小米手机Toast带应用名，e.g. "EasyDemo-EasyButterKnife:@OnClick"
        // 解决方法：需要先makeText传""，再setText
        val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        toast.setText("@OnClick${view.id}")
        toast.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        unBinder = ButterKnife.bind(this)
        Log.d(TAG, "onCreate: " + (textView == null))
//        textView?.setOnClickListener {
//            startActivity(Intent(this, MainExtendActivity::class.java))
//        }
    }

    override fun onDestroy() {
        unBinder.unbind()
        val bindingClass = classLoader.loadClass("com.easydemo.easybutterknife.MainActivityBinding")
        val activityField = bindingClass.getDeclaredField("activity")
        activityField.isAccessible = true
        Log.d(TAG, "onDestroy: " + (activityField.get(unBinder)))
        super.onDestroy()
    }
}