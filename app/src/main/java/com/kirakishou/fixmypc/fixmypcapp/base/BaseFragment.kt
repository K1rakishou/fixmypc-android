package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.myAddListener
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * Created by kirakishou on 7/30/2017.
 */
abstract class BaseFragment<T : ViewModel> : Fragment(), LifecycleRegistryOwner {

    private val mRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun getLifecycle(): LifecycleRegistry = mRegistry

    private var mUnBinder: Fickle<Unbinder> = Fickle.empty()
    protected var mViewModel: Fickle<T> = Fickle.empty()
    protected val mCompositeDisposable = CompositeDisposable()

    protected fun getViewModel(): T {
        return mViewModel.get()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Timber.d("onCreateView ${this::class.java.simpleName}")

        resolveDaggerDependency()
        mViewModel = Fickle.of(initViewModel())

        val viewId = getContentView()
        val root = inflater.inflate(viewId, container, false)
        mUnBinder = Fickle.of(ButterKnife.bind(this, root))

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runCallbackAfterAnimation(loadStartAnimations()) {
            onFragmentReady(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()

        Timber.d("onStart ${this::class.java.simpleName}")
    }

    override fun onResume() {
        super.onResume()

        Timber.d("onResume ${this::class.java.simpleName}")
    }

    override fun onPause() {
        Timber.d("onPause ${this::class.java.simpleName}")

        super.onPause()
    }

    override fun onStop() {
        Timber.d("onStop ${this::class.java.simpleName}")

        super.onStop()
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView ${this::class.java.simpleName}")

        AndroidUtils.hideSoftKeyboard(activity)
        mCompositeDisposable.clear()

        runCallbackAfterAnimation(loadExitAnimations()) {
            onFragmentStop()
        }

        mUnBinder.ifPresent {
            it.unbind()
        }

        super.onDestroyView()
    }

    protected fun showToast(message: String, duration: Int) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).onShowToast(message, duration)
    }

    protected open fun unknownError(throwable: Throwable) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity should implement BaseActivityFragmentCallback!")
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
    protected abstract fun onFragmentReady(savedInstanceState: Bundle?)
    protected abstract fun onFragmentStop()
    protected abstract fun resolveDaggerDependency()

    enum class FragmentEvent {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY
    }
}