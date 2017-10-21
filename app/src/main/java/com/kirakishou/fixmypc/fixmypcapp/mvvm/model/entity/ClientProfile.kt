package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/20/2017.
 */
data class ClientProfile(@Expose
                         @SerializedName("user_id")
                         var userId: Long = 0L,

                         @Expose
                         @SerializedName("name")
                         var name: String = "",

                         @Expose
                         @SerializedName("phone")
                         var phone: String = "")