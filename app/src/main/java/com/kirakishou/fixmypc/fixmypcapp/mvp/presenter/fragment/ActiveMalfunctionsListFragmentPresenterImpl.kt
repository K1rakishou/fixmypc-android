package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment

import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.ActiveMalfunctionsListFragmentView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class ActiveMalfunctionsListFragmentPresenterImpl
    @Inject constructor(protected val mApiClient: ApiClient) : ActiveMalfunctionsListFragmentPresenter<ActiveMalfunctionsListFragmentView>() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("ActiveMalfunctionsListFragmentPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("ActiveMalfunctionsListFragmentPresenterImpl.destroyPresenter()")
    }

    override fun getDamageClaims(page: Long) {
        mCompositeDisposable += mApiClient.getDamageClaims(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    private fun handleResponse(response: DamageClaimsResponse) {
        Timber.d("items size = ${response.damageClaims.size}")
    }

    private fun handleError(error: Throwable) {
        Timber.e(error)
    }
}