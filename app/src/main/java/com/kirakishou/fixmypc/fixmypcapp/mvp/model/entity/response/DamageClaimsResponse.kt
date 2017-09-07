package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/4/2017.
 */
data class DamageClaimsResponse(@Expose
                                @SerializedName("test")
                                val damageClaims: List<DamageClaim>,

                                @Expose
                                @SerializedName(Constant.SerializedNames.SERVER_ERROR_CODE)
                                val errorCode: ErrorCode.Remote)