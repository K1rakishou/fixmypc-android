package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.ClientNewDamageClaimActivityView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/27/2017.
 */

@Module
class ClientNewDamageClaimActivityModule(val mView: ClientNewDamageClaimActivityView) {

    @PerActivity
    @Provides
    fun provideView(): ClientNewDamageClaimActivityView {
        return mView
    }
}