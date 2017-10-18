package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/29/2017.
 */
class DamageClaimsClientResponse(@Expose
                                 @SerializedName("damage_claim_list")
                                 val damageClaims: MutableList<DamageClaim>,

                                 errorCode: ErrorCode.Remote) : StatusResponse(errorCode)