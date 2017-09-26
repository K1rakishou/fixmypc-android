package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 9/26/2017.
 */
class HasAlreadyRespondedResponse(@Expose
                                  @SerializedName("responded") val hasAlreadyResponded: Boolean,

                                  errorCode: ErrorCode.Remote) : StatusResponse(errorCode)