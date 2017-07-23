package com.kirakishou.fixmypc.fixmypcapp.base

import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
abstract class BasePresenter<V : BaseCallbacks> {
    @Inject
    lateinit protected var callbacks: V

    abstract fun initPresenter()
    abstract fun destroyPresenter()
}