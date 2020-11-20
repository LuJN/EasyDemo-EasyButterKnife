package com.easydemo.butterknife

import android.app.Activity
import com.easydemo.butterknife_runtime.Unbinder
import java.lang.reflect.Constructor

/**
 * @author lujunnan
 * @date 2020/11/19 2:38 PM
 * @decs
 */
class ButterKnife {
    companion object {
        private val BINDINGS = LinkedHashMap<Class<*>, Constructor<out Unbinder>>()

        fun bind(activity: Activity): Unbinder? {
            val bindingClassName = activity.javaClass.canonicalName + "Binding"
            val bindingClass = activity.classLoader.loadClass(bindingClassName)
            val constructor = BINDINGS[bindingClass] ?: try {
                // Class.forName会初始化静态块、静态变量，而ClassLoader.loadClass不会
//                val bindingClass = Class.forName(activity.javaClass.canonicalName + "Binding")
                bindingClass.getDeclaredConstructor(activity.javaClass).also {
                    BINDINGS[bindingClass] = it as Constructor<out Unbinder>
                }
            } catch(e: Exception) {
                e.printStackTrace()
                null
            }
            return constructor?.newInstance(activity) as Unbinder?
        }
    }
}