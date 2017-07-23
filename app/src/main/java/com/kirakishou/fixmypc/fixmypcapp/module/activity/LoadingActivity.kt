package com.kirakishou.fixmypc.fixmypcapp.module.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import butterknife.OnClick
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoadingActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.LoadingActivityPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import javax.inject.Inject

class LoadingActivity : BaseActivity(), LoadingActivityView {

    @Inject
    lateinit var mPresenter: LoadingActivityPresenterImpl

    override fun getContentView(): Int = R.layout.activity_loading

    override fun loadStartAnimations(): AnimatorSet {
        return AnimatorSet()
    }

    override fun loadExitAnimations(): AnimatorSet {
        return AnimatorSet()
    }

    override fun onPrepareView(savedInstanceState: Bundle?, intent: Intent) {
        super.onPrepareView(savedInstanceState, intent)
    }

    override fun onViewReady() {
        mPresenter.initPresenter()
    }

    override fun onViewStop() {
        mPresenter.destroyPresenter()
    }

    @OnClick(R.id.start_request_btn)
    fun onRequestBtnClick() {
        mPresenter.sendServiceMessage(ServiceMessage(Constant.EVENT_MESSAGE_TEST, TestRequestParams("test", "1234567890")))
    }

    override fun resolveDaggerDependency() {
        DaggerLoadingActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loadingActivityModule(LoadingActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    override fun onUnknownError(error: Throwable) {
        showToast(error.localizedMessage, Toast.LENGTH_LONG)
    }
}




















































