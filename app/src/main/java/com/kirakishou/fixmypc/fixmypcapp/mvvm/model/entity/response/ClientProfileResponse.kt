package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.ClientProfile

/**
 * Created by kirakishou on 9/20/2017.
 */
class ClientProfileResponse(@Expose
                            @SerializedName("client_profile")
                            val clientProfile: ClientProfile,

                            errorCode: ErrorCode.Remote) : StatusResponse(errorCode)