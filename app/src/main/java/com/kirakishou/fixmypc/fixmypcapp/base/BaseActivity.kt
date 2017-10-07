package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.myAddListener
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by kirakishou on 7/20/2017.
 */
abstract class BaseActivity<out T: ViewModel> : AppCompatActivity() {

    private val mRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun getLifecycle(): LifecycleRegistry = mRegistry

    protected val mCompositeDisposable = CompositeDisposable()
    private var mViewModel: Fickle<T> = Fickle.empty()
    private var mUnBinder: Fickle<Unbinder> = Fickle.empty()

    protected fun getViewModel(): T {
        return mViewModel.get()
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
        overridePendingTransitionExit()
        super.finish()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resolveDaggerDependency()
        mViewModel = Fickle.of(initViewModel())

        setContentView(getContentView())
        mUnBinder = Fickle.of(ButterKnife.bind(this))
        //Fabric.with(this, Crashlytics())

        onActivityCreate(savedInstanceState, intent)
    }

    override fun onStart() {
        super.onStart()

        animateActivityStart()
    }

    override fun onStop() {
        super.onStop()

        AndroidUtils.hideSoftKeyboard(this)

        animateActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        onActivityDestroy()
        mCompositeDisposable.clear()

        mUnBinder.ifPresent {
            it.unbind()
        }
    }

    private fun animateActivityStop() {
        runCallbackAfterAnimation(loadExitAnimations()) {
            onActivityStop()
        }
    }

    private fun animateActivityStart() {
        runCallbackAfterAnimation(loadStartAnimations()) {
            onActivityStart()
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

    protected open fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        runOnUiThread {
            Toast.makeText(this, message, duration).show()
        }
    }

    protected open fun onUnknownError(error: Throwable) {
        if (error.message != null) {
            showToast(error.message!!)
        } else {
            showToast("Неизвестная ошибка")
        }

        finish()
    }

    protected open fun runActivity(clazz: Class<*>, finishCurrentActivity: Boolean = false) {
        val intent = Intent(this, clazz)
        startActivity(intent)

        if (finishCurrentActivity) {
            finish()
        }
    }

    protected abstract fun initViewModel(): T?
    protected abstract fun getContentView(): Int
    protected abstract fun loadStartAnimations(): AnimatorSet
    protected abstract fun loadExitAnimations(): AnimatorSet
    protected abstract fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent)
    protected abstract fun onActivityDestroy()
    protected abstract fun onActivityStart()
    protected abstract fun onActivityStop()
    protected abstract fun resolveDaggerDependency()
}