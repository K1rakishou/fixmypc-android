package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.MalfunctionCategory

/**
 * Created by kirakishou on 8/1/2017.
 */
data class MalfunctionRequest(@SerializedName(Constant.SerializedNames.MALFUNCTION_CATEGORY) val category: MalfunctionCategory,
                              @SerializedName(Constant.SerializedNames.MALFUNCTION_DESCRIPTION) val description: String)