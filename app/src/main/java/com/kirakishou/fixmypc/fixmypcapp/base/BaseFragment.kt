package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.helper.MyViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.util.extension.myAddListener
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by kirakishou on 7/30/2017.
 */
abstract class BaseFragment<T : ViewModel> : LifecycleFragment() {

    @Inject
    protected lateinit var mViewModelProvider: MyViewModelProvider

    private var mUnBinder: Fickle<Unbinder> = Fickle.empty()
    protected var mViewModel: Fickle<T> = Fickle.empty()
    protected val mCompositeDisposable = CompositeDisposable()

    protected fun getViewModel(): T {
        return mViewModel.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resolveDaggerDependency()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewId = getContentView()
        val root = inflater.inflate(viewId, container, false)
        mUnBinder = Fickle.of(ButterKnife.bind(this, root))

        return root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = Fickle.of(mViewModelProvider.provideViewModel(this))
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runCallbackAfterAnimation(loadStartAnimations()) {
            onFragmentReady()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        AndroidUtils.hideSoftKeyboard(activity)
        mCompositeDisposable.clear()

        runCallbackAfterAnimation(loadExitAnimations()) {
            onFragmentStop()
        }

        mUnBinder.ifPresent {
            it.unbind()
        }
    }

    protected fun showToast(message: String) {
        if (activity !is BaseActivityFragmentCallback) {
            throw IllegalStateException("Activity should implement BaseActivityFragmentCallback!")
        }

        (activity as BaseActivityFragmentCallback).onShowToast(message)
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

    protected abstract fun getContentView(): Int
    protected abstract fun loadStartAnimations(): AnimatorSet
    protected abstract fun loadExitAnimations(): AnimatorSet
    protected abstract fun onFragmentReady()
    protected abstract fun onFragmentStop()
    protected abstract fun resolveDaggerDependency()
}