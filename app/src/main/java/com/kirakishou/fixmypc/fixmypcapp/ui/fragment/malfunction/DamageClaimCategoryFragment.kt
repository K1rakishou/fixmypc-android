package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerChooseCategoryActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewMalfunctionActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class DamageClaimCategoryFragment : BaseFragment<ClientNewMalfunctionActivityViewModel>() {

    @BindView(R.id.computer_category_button)
    lateinit var mComputerCategoryButton: AppCompatButton

    @BindView(R.id.notebook_category_button)
    lateinit var mNotebookCategoryButton: AppCompatButton

    @BindView(R.id.phone_category_button)
    lateinit var mPhoneCategoryButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    override fun initViewModel(): ClientNewMalfunctionActivityViewModel {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewMalfunctionActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_damage_claim_category
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initRx()
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(mComputerCategoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionCategory(DamageClaimCategory.Computer)
                    loadNextFragment()
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(mNotebookCategoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionCategory(DamageClaimCategory.Notebook)
                    loadNextFragment()
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(mPhoneCategoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionCategory(DamageClaimCategory.Phone)
                    loadNextFragment()
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun loadNextFragment() {
        mNavigator.navigateToDamageClaimDescriptionFragment()
    }

    private fun setMalfunctionCategory(category: DamageClaimCategory) {
        getViewModel().setCategory(category)
    }

    override fun onFragmentStop() {

    }

    override fun resolveDaggerDependency() {
        DaggerChooseCategoryActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(activity as ClientNewDamageClaimActivity))
                .build()
                .inject(this)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = DamageClaimCategoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
