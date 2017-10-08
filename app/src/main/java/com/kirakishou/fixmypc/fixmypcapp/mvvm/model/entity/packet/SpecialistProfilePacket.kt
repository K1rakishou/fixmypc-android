package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 10/8/2017.
 */
data class SpecialistProfilePacket(@Expose
                                   @SerializedName("profile_name")
                                   val profileName: String,

                                   @Expose
                                   @SerializedName("profile_photo")
                                   val profilePhone: String) : BasePacket