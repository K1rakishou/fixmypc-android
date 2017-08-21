package com.kirakishou.fixmypc.fixmypcapp.base

import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
abstract class BaseFragmentPresenter<V: BaseFragmentView> : BasePresenter() {
    @Inject
    lateinit var callbacks: V
}