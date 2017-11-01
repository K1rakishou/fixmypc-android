package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 11/1/2017.
 */
class AssignedSpecialistResponse(@Expose
                                 @SerializedName("specialist_user_id")
                                 val specialistUserId: Long,

                                 errorCode: ErrorCode.Remote) : StatusResponse(errorCode)