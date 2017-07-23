package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import com.kirakishou.fixmypc.fixmypcapp.extension.myAddListener
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


/**
 * Created by kirakishou on 7/20/2017.
 */
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var mEventBus: EventBus

    private val mCompositeDisposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    private fun overridePendingTransitionEnter() {
        overridePendingTransition(0, 0)
    }

    private fun overridePendingTransitionExit() {
        overridePendingTransition(0, 0)
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getContentView())
        ButterKnife.bind(this)
        //Fabric.with(this, Crashlytics())

        onPrepareView(savedInstanceState, intent)
    }

    @CallSuper
    protected open fun onPrepareView(savedInstanceState: Bundle?, intent: Intent) {
        resolveDaggerDependency()
    }

    override fun onStart() {
        super.onStart()

        mEventBus.register(this)
        animateActivityStart()
    }

    override fun onStop() {
        super.onStop()

        mCompositeDisposable.dispose()
        mEventBus.unregister(this)
        animateActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun animateActivityStop() {
        runCallbackAfterAnimation(loadExitAnimations()) {
            onViewStop()
        }
    }

    private fun animateActivityStart() {
        runCallbackAfterAnimation(loadStartAnimations()) {
            onViewReady()
        }
    }

    protected fun runCallbackAfterAnimation(set: AnimatorSet, onExitAnimationCallback: () -> Unit) {
        set.myAddListener {
            onAnimationEnd {
                onExitAnimationCallback()
            }
        }

        set.start()
    }

    protected fun showToast(message: String, duration: Int) {
        Toast.makeText(this, message, duration).show()
    }

    protected fun sendServiceMessage(message: ServiceMessage) {
        mEventBus.postSticky(message)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onServiceAnswer0(answer: ServiceAnswer) {
        onServiceAnswer(answer)
    }

    protected abstract fun getContentView(): Int

    protected abstract fun loadStartAnimations(): AnimatorSet

    protected abstract fun loadExitAnimations(): AnimatorSet

    protected abstract fun onViewReady()

    protected abstract fun onViewStop()

    protected abstract fun onServiceAnswer(answer: ServiceAnswer)

    protected abstract fun resolveDaggerDependency()
}