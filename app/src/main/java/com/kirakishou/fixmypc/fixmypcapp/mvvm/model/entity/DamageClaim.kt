package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 9/3/2017.
 */
data class DamageClaim(@Expose
                       @SerializedName("id") var id: Long = 0L,

                       @Expose
                       @SerializedName("owner_id") var ownerId: Long = 0L,

                       @Expose
                       @SerializedName("is_active") var isActive: Boolean = false,

                       @Expose
                       @SerializedName("category") var category: Int = 0,

                       @Expose
                       @SerializedName("description") var description: String = "",

                       @Expose
                       @SerializedName("lat") var lat: Double = 0.0,

                       @Expose
                       @SerializedName("lon") var lon: Double = 0.0,

                       @Expose
                       @SerializedName("created_on") var createdOn: Long = 0L,

                       @Expose
                       @SerializedName("photos") var photoNames: List<String> = mutableListOf())