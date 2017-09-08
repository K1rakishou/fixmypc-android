package com.kirakishou.fixmypc.fixmypcapp.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.LoadingActivityViewModelImpl
import javax.inject.Inject


/**
 * Created by kirakishou on 9/8/2017.
 */


class ViewModelFactory
@Inject constructor() : ViewModelProvider.Factory {

    @Inject
    protected lateinit var mLoadingActivityViewModel: LoadingActivityViewModelImpl

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        /*if (modelClass.isAssignableFrom(modelClass::class.java)) {
            return modelClass as T
        }*/

        //HACKHACKHACK
        return mLoadingActivityViewModel as T

        //throw IllegalArgumentException("unexpected viewModel class " + modelClass)
    }
}