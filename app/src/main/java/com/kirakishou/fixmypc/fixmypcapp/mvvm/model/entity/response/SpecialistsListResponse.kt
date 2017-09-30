package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile

/**
 * Created by kirakishou on 9/30/2017.
 */
class SpecialistsListResponse(@SerializedName("specialist_profiles_list")
                              val specialists: List<SpecialistProfile>,

                              errorCode: ErrorCode.Remote) : StatusResponse(errorCode)