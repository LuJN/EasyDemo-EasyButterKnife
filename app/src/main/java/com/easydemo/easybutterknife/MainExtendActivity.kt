package com.easydemo.easybutterknife

import android.util.Log
import com.easydemo.butterknife.ButterKnife
import com.easydemo.butterknife_runtime.Unbinder

/**
 * @author lujunnan
 * @date 2020/11/20 3:15 PM
 * @decs
 */
class MainExtendActivity : MainActivity() {
    companion object {
        private const val TAG = "MainExtendActivity"
    }

    private lateinit var unBinder: Unbinder

    override fun onResume() {
        super.onResume()
        unBinder = ButterKnife.bind(this)
        Log.d(TAG, "onResume: " + (textView == null))
    }

    override fun onDestroy() {
        unBinder.unbind()
        super.onDestroy()
    }
}