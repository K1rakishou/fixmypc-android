package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet

import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 11/2/2017.
 */
class MarkResponseViewedPacket(@SerializedName("damage_claim_id")
                               val damageClaimId: Long,

                               @SerializedName("user_id")
                               val userId: Long)