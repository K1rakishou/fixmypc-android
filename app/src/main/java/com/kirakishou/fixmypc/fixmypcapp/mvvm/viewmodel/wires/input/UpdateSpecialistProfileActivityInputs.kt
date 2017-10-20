package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input

/**
 * Created by kirakishou on 10/10/2017.
 */
interface UpdateSpecialistProfileActivityInputs {
    fun updateSpecialistProfilePhoto(photoPath: String)
    fun updateSpecialistProfileInfo(name: String, phone: String)
}