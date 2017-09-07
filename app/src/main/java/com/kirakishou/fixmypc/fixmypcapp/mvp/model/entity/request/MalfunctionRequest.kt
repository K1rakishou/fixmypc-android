package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant

/**
 * Created by kirakishou on 8/1/2017.
 */
data class MalfunctionRequest(@Expose
                              @SerializedName(Constant.SerializedNames.DAMAGE_CATEGORY) val category: Int,

                              @Expose
                              @SerializedName(Constant.SerializedNames.DAMAGE_DESCRIPTION) val description: String,

                              @Expose
                              @SerializedName(Constant.SerializedNames.LOCATION_LAT) val lat: Double,

                              @Expose
                              @SerializedName(Constant.SerializedNames.LOCATION_LON) val lom: Double) : BaseRequest