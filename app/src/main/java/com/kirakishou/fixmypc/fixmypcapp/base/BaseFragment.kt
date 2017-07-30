package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.util.extension.myAddListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by kirakishou on 7/30/2017.
 */
abstract class BaseFragment : Fragment() {

    private var mUnbinder: Fickle<Unbinder> = Fickle.empty()
    private val mCompositeDisposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //resolveDaggerDependency()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewId = getContentView()
        val root = inflater!!.inflate(viewId, container, false)
        mUnbinder = Fickle.of(ButterKnife.bind(this, root))

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mUnbinder.ifPresent {
            it.unbind()
        }
    }

    override fun onStart() {
        super.onStart()

        runCallbackAfterAnimation(loadStartAnimations()) {
            onFragmentReady()
        }
    }

    override fun onStop() {
        super.onStop()
        mCompositeDisposable.clear()

        runCallbackAfterAnimation(loadExitAnimations()) {
            onFragmentStop()
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

    protected abstract fun getContentView(): Int
    protected abstract fun loadStartAnimations(): AnimatorSet
    protected abstract fun loadExitAnimations(): AnimatorSet
    protected abstract fun onFragmentReady()
    protected abstract fun onFragmentStop()

    //abstract fun resolveDaggerDependency()
}