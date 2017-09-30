package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/25/2017.
 */
data class RespondToDamageClaimPacket(@Expose
                                      @SerializedName("damage_claim_id")
                                      val damageClaimId: Long) : BasePacket