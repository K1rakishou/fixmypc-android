package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/20/2017.
 */
data class ClientProfile(@SerializedName("user_id")
                         val userId: Long = 0L,

                         @SerializedName("name")
                         val name: String = "",

                         @SerializedName("phone")
                         val phone: String = "")