package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionApplicationInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.rxholder.RequestAndPhotoParts
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import com.kirakishou.fixmypc.fixmypcapp.util.retrofit.ProgressRequestBody
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiStoreImpl
    @Inject constructor(val mApiService: ApiService,
                        val errorBodyConverter: ErrorBodyConverter) : FixmypcApiStore {

    override var callbacks: Fickle<BackgroundServicePresenter> = Fickle.empty()
    private val mCompositeDisposable = CompositeDisposable()

    override fun cleanup() {
        mCompositeDisposable.clear()
    }

    override fun loginRequest(loginRequest: LoginRequest, type: ServiceMessageType) {
        mCompositeDisposable += mApiService.doLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ answer ->
                    callbacks.ifPresent {
                        it.returnAnswer(ServiceAnswer(type, ServerResponse.Success(answer)))
                    }
                }, { error ->
                    Timber.e(error)

                    if (error is HttpException) {
                        val response = errorBodyConverter.convert<LoginResponse>(error.response().errorBody()!!.string(), LoginResponse::class.java)
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.ServerError(response.errorCode)))
                        }
                    } else {
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.UnknownError(error)))
                        }
                    }
                })
    }

    override fun sendMalfunctionRequest(malfunctionApplicationInfo: MalfunctionApplicationInfo, type: ServiceMessageType) {
        mCompositeDisposable += Single.just(malfunctionApplicationInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { requestInfo ->
                    if (!requestInfo.malfunctionPhotos.isPresent()) {
                        return@flatMap Single.just(RxValue.error(ErrorCode.Local.LEC_MAI_PHOTOS_ARE_NOT_SET))
                    }

                    if (!requestInfo.malfunctionCategory.isPresent()) {
                        return@flatMap Single.just(RxValue.error(ErrorCode.Local.LEC_MAI_CATEGORY_IS_NOT_SET))
                    }

                    if (!requestInfo.malfunctionDescription.isPresent()) {
                        return@flatMap Single.just(RxValue.error(ErrorCode.Local.LEC_MAI_DESCRIPTION_IS_NOT_SET))
                    }

                    val photoPaths = malfunctionApplicationInfo.malfunctionPhotos.get()
                    val multipartBodyPartsList = arrayListOf<MultipartBody.Part>()

                    for (photoPath in photoPaths) {
                        val photoFile = File(photoPath)
                        if (photoFile.length() > Constant.MAX_FILE_SIZE) {
                            return@flatMap Single.just(RxValue.error(ErrorCode.Local.LEC_FILE_SIZE_EXCEEDED))
                        }

                        if (!photoFile.isFile || !photoFile.exists() || photoFile.isDirectory) {
                            return@flatMap Single.just(RxValue.error(ErrorCode.Local.LEC_SELECTED_PHOTO_DOES_NOT_EXIST))
                        }

                        //val requestBody = RequestBody.create(MediaType.parse("image/*"), photoFile)
                        val progressRequestBody = ProgressRequestBody(photoFile)
                        mCompositeDisposable += progressRequestBody.getProgressSubject()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ percent ->
                                    Timber.d("percent: $percent")
                                }, { error ->
                                    Timber.e(error)
                                }, {
                                    Timber.d("=== DONE ===")
                                })

                        multipartBodyPartsList.add(MultipartBody.Part.createFormData("photos", photoFile.name, progressRequestBody))
                    }

                    val request = MalfunctionRequest(requestInfo.malfunctionCategory.get().ordinal,
                            requestInfo.malfunctionDescription.get())

                    return@flatMap Single.just(RxValue.value(RequestAndPhotoParts(request, multipartBodyPartsList)))
                }
                .flatMap { requestAndPhotoParts ->
                    if (requestAndPhotoParts.isError()) {
                        return@flatMap Single.just(RxValue.error(requestAndPhotoParts.error.get()))
                    }

                    val requestAndPhotos = requestAndPhotoParts.value.get() as RequestAndPhotoParts

                    return@flatMap mApiService.sendMalfunctionRequest(requestAndPhotos.photoParts.toTypedArray(),
                            requestAndPhotos.request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                }
                .subscribe({ answer ->
                    if (answer is RxValue<*>) {
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.LocalError(answer.error.get())))
                        }
                    } else if (answer is MalfunctionResponse) {
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.Success(answer)))
                        }
                    }
                }, { error ->
                    Timber.e(error)

                    if (error is HttpException) {
                        val response = errorBodyConverter.convert<MalfunctionResponse>(error.response().errorBody()!!.string(), MalfunctionResponse::class.java)
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.ServerError(response.errorCode)))
                        }
                    } else {
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.UnknownError(error)))
                        }
                    }
                })
    }
}