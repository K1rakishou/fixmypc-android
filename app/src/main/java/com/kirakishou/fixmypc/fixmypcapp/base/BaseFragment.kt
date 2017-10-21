package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.hideKeyboard
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.myAddListener
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * Created by kirakishou on 7/30/2017.
 */
abstract class BaseFragment<T : ViewModel> : Fragment() {

    protected val mRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun getLifecycle(): LifecycleRegistry = mRegistry

    private lateinit var mUnBinder: Unbinder
    protected var mViewModel: Fickle<T> = Fickle.empty()
    protected val mCompositeDisposable = CompositeDisposable()

    protected fun getViewModel(): T {
        return mViewModel.get()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        resolveDaggerDependency()
        mViewModel = Fickle.of(initViewModel())

        val viewId = getContentView()
        val root = inflater.inflate(viewId, container, false)
        mUnBinder = ButterKnife.bind(this, root)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runCallbackAfterAnimation(loadStartAnimations()) {
            onFragmentViewCreated(savedInstanceState)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        mCompositeDisposable.clear()

        runCallbackAfterAnimation(loadExitAnimations()) {
            onFragmentViewDestroy()
        }

        mUnBinder.unbind()
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()

        hideKeyboard()
    }

    protected fun runActivityWithArgs(clazz: Class<*>, args: Bundle, finishCurrentActivity: Boolean = false) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity ${activity::class.simpleName} should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).runActivityWithArgs(clazz, args, finishCurrentActivity)
    }

    protected fun runActivity(clazz: Class<*>, finishCurrentActivity: Boolean = false) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity ${activity::class.simpleName} should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).runActivity(clazz, finishCurrentActivity)
    }

    protected fun finishActivity() {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity ${activity::class.simpleName} should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).finishActivity()
    }

    protected fun sendBroadcast(intent: Intent) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity ${activity::class.simpleName} should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).sendBroadcast(intent)
    }

    protected fun showToast(message: String, duration: Int) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity ${activity::class.simpleName} should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).onShowToast(message, duration)
    }

    protected open fun unknownError(throwable: Throwable) {
        Timber.e(throwable)

        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity ${activity::class.simpleName} should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).onUnknownError(throwable)
    }

    protected fun runCallbackAfterAnimation(set: AnimatorSet, onExitAnimationCallback: () -> Unit) {
        set.myAddListener {
            onAnimationEnd {
                onExitAnimationCallback()
            }
        }

        set.start()
    }

    protected abstract fun initViewModel(): T?
    protected abstract fun getContentView(): Int
    protected abstract fun loadStartAnimations(): AnimatorSet
    protected abstract fun loadExitAnimations(): AnimatorSet
    protected abstract fun onFragmentViewCreated(savedInstanceState: Bundle?)
    protected abstract fun onFragmentViewDestroy()
    protected abstract fun onBadResponse(errorCode: ErrorCode.Remote)
    protected abstract fun onUnknownError(error: Throwable)
    protected abstract fun resolveDaggerDependency()
}