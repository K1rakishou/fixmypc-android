package com.kirakishou.fixmypc.fixmypcapp.base

/**
 * Created by kirakishou on 7/23/2017.
 */
abstract class BaseActivityPresenter<V : BaseCallbacks> : BasePresenter<V>() {
    abstract fun initPresenter()
    abstract fun destroyPresenter()
}