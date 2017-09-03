package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.main


import android.animation.AnimatorSet
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerMyMalfunctionRequestsFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.MyMalfunctionRequestsFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment.MyMalfunctionRequestsFragmentPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.MyMalfunctionRequestsFragmentView
import javax.inject.Inject


class MyMalfunctionRequestsFragment : BaseFragment(), MyMalfunctionRequestsFragmentView {

    @Inject
    lateinit var mPresenter: MyMalfunctionRequestsFragmentPresenterImpl

    override fun getContentView() = R.layout.fragment_my_malfunction_requests
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {

    }

    override fun onFragmentStop() {

    }

    override fun onShowToast(message: String) {
        showToast(message)
    }

    override fun onUnknownError(throwable: Throwable) {
        onUnknownError0(throwable)
    }

    override fun resolveDaggerDependency() {
        DaggerMyMalfunctionRequestsFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .myMalfunctionRequestsFragmentModule(MyMalfunctionRequestsFragmentModule(this))
                .build()
                .inject(this)
    }
}
