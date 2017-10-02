package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.RespondedSpecialistsActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.RespondedSpecialistsActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.responded_specialists.RespondedSpecialistFullProfileFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.responded_specialists.RespondedSpecialistsListFragment
import dagger.Component

/**
 * Created by kirakishou on 10/2/2017.
 */

@PerActivity
@Component(modules = arrayOf(RespondedSpecialistsActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface RespondedSpecialistsActivityComponent {
    fun inject(activity: RespondedSpecialistsActivity)
    fun inject(fragment: RespondedSpecialistsListFragment)
    fun inject(fragment: RespondedSpecialistFullProfileFragment)
}