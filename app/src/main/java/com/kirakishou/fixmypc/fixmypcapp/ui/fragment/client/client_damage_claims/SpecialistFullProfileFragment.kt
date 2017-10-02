package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import javax.inject.Inject

class SpecialistFullProfileFragment : BaseFragment<ClientMainActivityViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    override fun initViewModel(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_specialist_full_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
    }

    override fun onFragmentViewDestroy() {
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule(activity as ClientMainActivity))
                .build()
                .inject(this)
    }
}
