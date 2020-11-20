package com.easydemo.butterknife

import android.app.Activity
import com.easydemo.butterknife_runtime.Unbinder

/**
 * @author lujunnan
 * @date 2020/11/19 2:38 PM
 * @decs
 */
class ButterKnife {
    companion object {
        fun bind(activity: Activity): Unbinder? {
            return try {
                // Class.forName会初始化静态块、静态变量，而ClassLoader.loadClass不会
//                val bindingClass = Class.forName(activity.javaClass.canonicalName + "Binding")
                val bindingClass = activity.classLoader.loadClass(activity.javaClass.canonicalName + "Binding")
                val constructor  = bindingClass.getDeclaredConstructor(activity.javaClass)
                constructor.newInstance(activity) as Unbinder?
            } catch(e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}