package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile

/**
 * Created by kirakishou on 10/7/2017.
 */
class SpecialistProfileResponse(@Expose
                                @SerializedName("specialist_profile")
                                val profile: SpecialistProfile,

                                @Expose
                                @SerializedName("is_profile_filled")
                                var isProfileFilledIn: Boolean = false,

                                errorCode: ErrorCode.Remote) : StatusResponse(errorCode)