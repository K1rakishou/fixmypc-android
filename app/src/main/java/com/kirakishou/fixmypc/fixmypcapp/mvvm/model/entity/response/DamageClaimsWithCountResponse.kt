package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimResponseCount

/**
 * Created by kirakishou on 10/18/2017.
 */
class DamageClaimsWithCountResponse(@Expose
                                    @SerializedName("damage_claim_list")
                                    val damageClaims: MutableList<DamageClaim>,

                                    @Expose
                                    @SerializedName("responses_count_list")
                                    val responsesCountList: List<DamageClaimResponseCount>,

                                    errorCode: ErrorCode.Remote) : StatusResponse(errorCode)