package com.easydemo.butterknife

import android.app.Activity
import android.util.Log
import android.view.View
import com.easydemo.butterknife_runtime.Unbinder
import java.lang.reflect.Constructor

/**
 * @author lujunnan
 * @date 2020/11/19 2:38 PM
 * @decs
 */
object ButterKnife {
    private const val TAG = "ButterKnife"
    private var debug = false
    private val BINDINGS = LinkedHashMap<Class<*>, Constructor<out Unbinder>?>()

    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    fun bind(target: Any, source: View): Unbinder {
        val constructor = findBindingConstructorForClass(target.javaClass)
        return constructor?.newInstance(target, source) ?: Unbinder.EMPTY
    }

    fun bind(activity: Activity): Unbinder {
        return bind(activity, activity.window.decorView)
    }

    private fun findBindingConstructorForClass(clazz: Class<*>): Constructor<out Unbinder>? {
        var constructor = BINDINGS[clazz]
        if(constructor != null) {
            return constructor
        }
        val className = clazz.name
        if(className.startsWith("android.") || className.startsWith("androidx.") ||
                className.startsWith("java.")) {
            if(debug) {
                Log.d(TAG, "findBindingConstructorForClass: className startWith android/androidx/java")
            }
            return null
        }
        constructor = try {
            val bindingClassName = className + "Binding"
            val bindingClass = clazz.classLoader.loadClass(bindingClassName)
            bindingClass.getDeclaredConstructor(clazz, View::class.java) as Constructor<out Unbinder>
        } catch(e: ClassNotFoundException) {
            e.printStackTrace()
            if(debug) {
                Log.d(TAG, "findBindingConstructorForClass: try superclass")
            }
            findBindingConstructorForClass(clazz.superclass)
        } catch(e: Exception) {
            e.printStackTrace()
            if(debug) {
                Log.d(TAG, "findBindingConstructorForClass: don't find")
            }
            null
        }
        BINDINGS[clazz] = constructor
        return constructor
    }
}