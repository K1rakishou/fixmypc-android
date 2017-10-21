package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 10/20/2017.
 */
data class ClientProfilePacket(@Expose
                               @SerializedName("profile_name")
                               val profileName: String,

                               @Expose
                               @SerializedName("profile_phone")
                               val profilePhone: String)