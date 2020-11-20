package com.easydemo.easybutterknife

import android.util.Log
import com.easydemo.butterknife.ButterKnife

/**
 * @author lujunnan
 * @date 2020/11/20 3:15 PM
 * @decs
 */
class MainExtendActivity : MainActivity() {
    companion object {
        private const val TAG = "MainExtendActivity"
    }

    override fun onResume() {
        super.onResume()
        ButterKnife.bind(this)
        Log.d(TAG, "onResume: " + (textView == null))
    }
}