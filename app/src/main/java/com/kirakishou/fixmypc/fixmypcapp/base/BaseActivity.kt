package com.kirakishou.fixmypc.fixmypcapp.base

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.Unbinder
import com.afollestad.materialdialogs.MaterialDialog
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.util.extension.myAddListener
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by kirakishou on 7/20/2017.
 */
abstract class BaseActivity : AppCompatActivity() {

    val mCompositeDisposable = CompositeDisposable()
    private var mUnBinder: Fickle<Unbinder> = Fickle.empty()

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
        mUnBinder = Fickle.of(ButterKnife.bind(this))
        //Fabric.with(this, Crashlytics())

        onPrepareView(savedInstanceState, intent)
    }

    @CallSuper
    protected open fun onPrepareView(savedInstanceState: Bundle?, intent: Intent) {
        resolveDaggerDependency()
        onInitPresenter()
    }

    override fun onStart() {
        super.onStart()

        animateActivityStart()
    }

    override fun onStop() {
        super.onStop()

        AndroidUtils.hideSoftKeyboard(this)
        mCompositeDisposable.clear()
        animateActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        onDestroyPresenter()

        mUnBinder.ifPresent {
            it.unbind()
        }
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

    protected fun showErrorMessageDialog(message: String, finishActivity: Boolean = false) {
        MaterialDialog.Builder(this)
                .title(R.string.rec_unknown_server_error_has_occurred)
                .content(message)
                .positiveText(R.string.ok)
                .onPositive { _, _ ->
                    if (finishActivity) {
                        finish()
                    }
                }
                .show()
    }

    protected fun runActivity(clazz: Class<*>, finishCurrentActivity: Boolean = false) {
        val intent = Intent(this, clazz)
        startActivity(intent)

        if (finishCurrentActivity) {
            finish()
        }
    }

    protected abstract fun getContentView(): Int
    protected abstract fun loadStartAnimations(): AnimatorSet
    protected abstract fun loadExitAnimations(): AnimatorSet
    protected abstract fun onInitPresenter()
    protected abstract fun onDestroyPresenter()
    protected abstract fun onViewReady()
    protected abstract fun onViewStop()
    protected abstract fun resolveDaggerDependency()
}