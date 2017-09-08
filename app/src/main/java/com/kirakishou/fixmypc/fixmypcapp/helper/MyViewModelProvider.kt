package com.kirakishou.fixmypc.fixmypcapp.helper

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import com.kirakishou.fixmypc.fixmypcapp.di.module.ViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.helper.annotation.RequiresViewModel
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Created by kirakishou on 9/8/2017.
 */


class MyViewModelProvider
@Inject constructor(val mViewModelFactory: ViewModelFactory) {

    fun <T : ViewModel> provideViewModel(activity: LifecycleActivity): T {
        if (!activity.javaClass.isAnnotationPresent(RequiresViewModel::class.java)) {
            throw RuntimeException("Activity ${this.javaClass} does not have RequiresViewModel annotation!!!")
        }

        val annotation = activity.javaClass.getAnnotation(RequiresViewModel::class.java) as RequiresViewModel
        val viewModelClass = annotation.viewModelClass as KClass<T>

        return ViewModelProviders.of(activity, mViewModelFactory).get(viewModelClass.java)
    }

    fun <T : ViewModel> provideViewModel(fragment: LifecycleFragment): T {
        if (!fragment.javaClass.isAnnotationPresent(RequiresViewModel::class.java)) {
            throw RuntimeException("Activity ${this.javaClass} does not have RequiresViewModel annotation!!!")
        }

        val annotation = fragment.javaClass.getAnnotation(RequiresViewModel::class.java) as RequiresViewModel
        val viewModelClass = annotation.viewModelClass as KClass<T>

        return ViewModelProviders.of(fragment, mViewModelFactory).get(viewModelClass.java)
    }
}