package com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by kirakishou on 9/17/2017.
 */
class TestSchedulers : SchedulerProvider {
    override fun provideIo(): Scheduler = Schedulers.trampoline()
    override fun provideComputation(): Scheduler = Schedulers.trampoline()
    override fun provideMain(): Scheduler = Schedulers.trampoline()
}