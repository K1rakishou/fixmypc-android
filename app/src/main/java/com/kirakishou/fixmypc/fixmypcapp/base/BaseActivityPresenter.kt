package com.kirakishou.fixmypc.fixmypcapp.base

import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
abstract class BaseActivityPresenter<V : BaseActivityView> : BasePresenter() {
    @Inject
    lateinit var callbacks: V
}