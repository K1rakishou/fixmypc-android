package com.kirakishou.fixmypc.fixmypcapp.module.fragment


import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.module.activity.ClientMainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber


class MalfunctionPhotosFragment : BaseFragment() {

    @BindView(R.id.button_send_application)
    lateinit var mButtonSendApplication: AppCompatButton

    override fun getContentView() = R.layout.fragment_malfunction_photos
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initBindings()
    }

    private fun initBindings() {
        addDisposable(RxView.clicks(mButtonSendApplication)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    sendApplication()
                }, { error ->
                    Timber.e(error)
                }))
    }

    private fun sendApplication() {
        val activityHolder = activity as ClientMainActivity
        activityHolder.sendApplication()
    }

    override fun onFragmentStop() {

    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = MalfunctionPhotosFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
