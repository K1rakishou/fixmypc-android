package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/20/2017.
 */
data class ClientProfile(@Expose
                         @SerializedName("user_id")
                         val userId: Long = 0L,

                         @Expose
                         @SerializedName("name")
                         val name: String = "",

                         @Expose
                         @SerializedName("phone")
                         val phone: String = "",

                         @Expose
                         @SerializedName("photo_folder")
                         val photoFolder: String = "",

                         @Expose
                         @SerializedName("photo_name")
                         val photoName: String = "")