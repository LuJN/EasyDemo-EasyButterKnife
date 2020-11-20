package com.easydemo.butterknife

import android.app.Activity

/**
 * @author lujunnan
 * @date 2020/11/19 2:38 PM
 * @decs
 */
class ButterKnife {
    companion object {
        fun bind(activity: Activity) {
            try {
                // Class.forName会初始化静态块、静态变量，而ClassLoader.loadClass不会
//                val bindingClass = Class.forName(activity.javaClass.canonicalName + "Binding")
                val bindingClass = activity.classLoader.loadClass(activity.javaClass.canonicalName + "Binding")
                val constructor  = bindingClass.getDeclaredConstructor(activity.javaClass)
                constructor.newInstance(activity)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }
}