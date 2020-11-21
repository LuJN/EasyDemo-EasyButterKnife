package com.easydemo.butterknife_annotations

/**
 * @author lujunnan
 * @date 2020/11/20 8:45 PM
 * @decs
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class OnClick(vararg val values: Int)