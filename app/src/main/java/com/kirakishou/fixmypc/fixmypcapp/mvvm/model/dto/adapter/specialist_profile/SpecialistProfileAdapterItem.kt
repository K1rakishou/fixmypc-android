package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile

/**
 * Created by kirakishou on 10/1/2017.
 */
class SpecialistProfileAdapterItem(val userId: Long,
                                   val name: String,
                                   val rating: Float,
                                   val photoName: String,
                                   val registeredOn: Long,
                                   val successRepairs: Int,
                                   val failRepairs: Int) : SpecialistProfileGenericParam()