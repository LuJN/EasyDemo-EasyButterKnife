package com.easydemo.easybutterknife

import android.app.Application
import com.easydemo.butterknife.ButterKnife

/**
 * @author lujunnan
 * @date 2020/11/20 3:12 PM
 * @decs
 */
class EasyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ButterKnife.setDebug(BuildConfig.DEBUG)
    }
}