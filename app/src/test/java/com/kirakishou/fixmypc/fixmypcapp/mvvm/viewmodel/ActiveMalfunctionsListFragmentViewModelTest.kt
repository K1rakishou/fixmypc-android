package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.BaseRobolectricTestCase
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.TestSchedulers
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

/**
 * Created by kirakishou on 9/17/2017.
 */
class ActiveMalfunctionsListFragmentViewModelTest : BaseRobolectricTestCase() {

    //inputs
    private val mRequestParamsSubject = BehaviorSubject.create<ActiveMalfunctionsListFragmentViewModel.GetDamageClaimsRequestParamsDTO>()
    private val mIsFirstFragmentStartSubject = BehaviorSubject.create<Boolean>()

    //outputs
    private val mOnDamageClaimsPageReceivedSubject = BehaviorSubject.create<ArrayList<DamageClaimsWithDistanceDTO>>()

    //errors
    private val mOnNothingFoundSubject = BehaviorSubject.create<Unit>()
    private val mOnUnknownErrorSubject = BehaviorSubject.create<Throwable>()
    private val mSendRequestSubject = BehaviorSubject.create<ActiveMalfunctionsListFragmentViewModel.GetDamageClaimsRequestParamsDTO>()
    private val mEitherFromRepoOrServerSubject = BehaviorSubject.create<Pair<LatLng, DamageClaimsResponse>>()

    val mSchedulers = TestSchedulers()
    val mViewModel = ActiveMalfunctionsListFragmentViewModel(provideFixmypcApiStore(), provideNetworkManager(), provideDamageClaimRepository(), mSchedulers)

    @Before
    fun init() {
        mViewModel.mOutputs.onDamageClaimsPageReceived().subscribe(mOnDamageClaimsPageReceivedSubject)
    }

    @Test
    fun test() {

    }
}











































