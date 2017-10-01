package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/30/2017.
 */
data class SpecialistProfile(@Expose
                             @SerializedName("user_id")
                             val userId: Long,

                             @Expose
                             @SerializedName("name")
                             val name: String,

                             @Expose
                             @SerializedName("rating")
                             val rating: Float,

                             @Expose
                             @SerializedName("photo_name")
                             val photoName: String,

                             @Expose
                             @SerializedName("registered_on")
                             val registeredOn: Long,

                             @Expose
                             @SerializedName("success_repairs")
                             val successRepairs: Int,

                             @Expose
                             @SerializedName("fail_repairs")
                             val failRepairs: Int)