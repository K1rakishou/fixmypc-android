package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 11/1/2017.
 */
class RespondedSpecialist(@Expose
                          @SerializedName("id")
                          var id: Long = -1L,

                          @Expose
                          @SerializedName("user_id")
                          val userId: Long = -1L,

                          @Expose
                          @SerializedName("damage_claim_id")
                          val damageClaimId: Long = -1L,

                          @Expose
                          @SerializedName("was_viewed")
                          val wasViewed: Boolean = false) {

    fun compareTo(other: RespondedSpecialist): Int {
        if (this.damageClaimId > other.damageClaimId) {
            return 1
        }

        if (this.damageClaimId < other.damageClaimId) {
            return -1
        }

        return 0
    }
}