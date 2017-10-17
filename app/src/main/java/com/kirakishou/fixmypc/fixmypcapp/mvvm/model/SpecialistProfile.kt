package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/30/2017.
 */
data class SpecialistProfile(@Expose
                             @SerializedName("user_id")
                             var userId: Long = 0L,

                             @Expose
                             @SerializedName("name")
                             var name: String = "",

                             @Expose
                             @SerializedName("rating")
                             var rating: Float = 0f,

                             @Expose
                             @SerializedName("photo_name")
                             var photoName: String = "",

                             @Expose
                             @SerializedName("phone")
                             var phone: String = "",

                             @Expose
                             @SerializedName("registered_on")
                             var registeredOn: Long = 0L,

                             @Expose
                             @SerializedName("success_repairs")
                             var successRepairs: Int = 0,

                             @Expose
                             @SerializedName("fail_repairs")
                             var failRepairs: Int = 0)