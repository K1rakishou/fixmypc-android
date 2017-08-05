package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.MalfunctionCategory

/**
 * Created by kirakishou on 8/1/2017.
 */
class MalfunctionApplicationInfo {
    var malfunctionCategory: Fickle<MalfunctionCategory> = Fickle.empty()
    var malfunctionDescription: Fickle<String> = Fickle.empty()
    var malfunctionPhotos: Fickle<ArrayList<String>> = Fickle.empty()
}