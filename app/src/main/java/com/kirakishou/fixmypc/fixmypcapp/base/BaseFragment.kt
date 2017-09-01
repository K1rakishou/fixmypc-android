package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.util.extension.myAddListener
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by kirakishou on 7/30/2017.
 */
abstract class BaseFragment : Fragment() {

    private var mUnBinder: Fickle<Unbinder> = Fickle.empty()
    val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resolveDaggerDependency()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewId = getContentView()
        val root = inflater!!.inflate(viewId, container, false)
        mUnBinder = Fickle.of(ButterKnife.bind(this, root))

        return root
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