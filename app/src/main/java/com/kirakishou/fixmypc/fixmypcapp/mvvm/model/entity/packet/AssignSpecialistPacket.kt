package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 10/2/2017.
 */
class AssignSpecialistPacket(@Expose
                             @SerializedName("specialist_user_id")
                             val specialistUserId: Long,

                             @Expose
                             @SerializedName("damage_claim_id")
                             val damageClaimId: Long) : BasePacket