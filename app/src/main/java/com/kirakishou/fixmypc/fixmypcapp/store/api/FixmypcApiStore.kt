package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.util.dialog.FileUploadProgressUpdater
import io.reactivex.Single
import java.lang.ref.WeakReference

/**
 * Created by kirakishou on 7/23/2017.
 */
interface FixmypcApiStore {
    fun loginRequest(loginRequest: LoginRequest): Single<LoginResponse>
    fun sendMalfunctionRequest(malfunctionRequestInfo: MalfunctionRequestInfo,
                               uploadProgressCallback: WeakReference<FileUploadProgressUpdater>): Single<MalfunctionResponse>
}