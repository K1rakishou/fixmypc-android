package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 10/17/2017.
 */
class IsProfileFilledInResponse(@Expose
                                @SerializedName("is_profile_filled")
                                val isProfileFilledIn: Boolean = false,

                                errorCode: ErrorCode.Remote) : StatusResponse(errorCode)