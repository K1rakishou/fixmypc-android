package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerService
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStore
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStoreImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by kirakishou on 7/22/2017.
 */

@Module
class BackgroundServiceModule(val mCallbacks: BackgroundServiceCallbacks) {

    @PerService
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @PerService
    @Provides
    fun provideCallbacks(): BackgroundServiceCallbacks {
        return mCallbacks
    }

    @PerService
    @Provides
    fun provideRequestFactory(apiService: ApiService, retrofit: Retrofit): FixmypcApiStore {
        return FixmypcApiStoreImpl(apiService, retrofit)
    }
}