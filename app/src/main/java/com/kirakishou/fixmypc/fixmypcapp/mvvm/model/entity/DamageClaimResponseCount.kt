package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 10/18/2017.
 */
data class DamageClaimResponseCount(@Expose
                                    @SerializedName("damage_claim_id")
                                    val damageClaimId: Long,

                                    @Expose
                                    @SerializedName("response_count")
                                    val responseCount: Int)