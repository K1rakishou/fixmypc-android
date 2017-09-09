package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/4/2017.
 */
data class DamageClaimsResponse(@Expose
                                @SerializedName("test")
                                val damageClaims: List<DamageClaim>,

                                @Expose
                                @SerializedName(Constant.SerializedNames.SERVER_ERROR_CODE)
                                val errorCode: ErrorCode.Remote)