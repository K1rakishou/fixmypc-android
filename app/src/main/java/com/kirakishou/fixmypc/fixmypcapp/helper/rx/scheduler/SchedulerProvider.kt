package com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler

import io.reactivex.Scheduler

/**
 * Created by kirakishou on 9/17/2017.
 */
interface SchedulerProvider {
    fun provideIo(): Scheduler
    fun provideComputation(): Scheduler
    fun provideMain(): Scheduler
}