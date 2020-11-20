package com.easydemo.butterknife_runtime

/**
 * @author lujunnan
 * @date 2020/11/20 11:02 AM
 * @decs
 */
interface Unbinder {
    fun unbind()

    object EMPTY : Unbinder {
        override fun unbind() {}
    }
}