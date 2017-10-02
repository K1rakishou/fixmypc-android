package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.RespondedSpecialistsActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.RespondedSpecialistsActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.RespondedSpecialistsActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/2/2017.
 */
class RespondedSpecialistsViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        RespondedSpecialistsActivityInputs,
        RespondedSpecialistsActivityOutputs,
        RespondedSpecialistsActivityErrors {

    val mInputs: RespondedSpecialistsActivityInputs = this
    val mOutputs: RespondedSpecialistsActivityOutputs = this
    val mErrors: RespondedSpecialistsActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>
    lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    fun init() {
        mOnBadResponseSubject = BehaviorSubject.create()
        mOnUnknownErrorSubject = BehaviorSubject.create()
    }

    override fun onCleared() {
        super.onCleared()

        Timber.e("RespondedSpecialistsViewModel.onCleared()")
        mCompositeDisposable.clear()
    }

    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}