package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.main


import android.animation.AnimatorSet
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerMyDamageClaimsFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.MyDamageClaimsFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.helper.annotation.RequiresViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.MyMalfunctionRequestsFragmentPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.MyDamageClaimsFragmentView

@RequiresViewModel(MyMalfunctionRequestsFragmentPresenterImpl::class)
class MyMalfunctionRequestsFragment : BaseFragment<MyMalfunctionRequestsFragmentPresenterImpl>(), MyDamageClaimsFragmentView {

    /*@Inject
    lateinit var mPresenter: MyMalfunctionRequestsFragmentPresenterImpl*/

    override fun getContentView() = R.layout.fragment_my_damage_claims
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
        unknownError(throwable)
    }

    override fun resolveDaggerDependency() {
        DaggerMyDamageClaimsFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .myDamageClaimsFragmentModule(MyDamageClaimsFragmentModule(this))
                .build()
                .inject(this)
    }
}
