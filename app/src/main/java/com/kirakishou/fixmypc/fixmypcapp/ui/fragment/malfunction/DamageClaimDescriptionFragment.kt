package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.CardView
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber


class DamageClaimDescriptionFragment : BaseFragment<Nothing>() {

    @BindView(R.id.card_view)
    lateinit var mViewHolderCardView: CardView

    @BindView(R.id.damage_claim_description)
    lateinit var mDamageClaimDescriptionEditText: TextInputEditText

    @BindView(R.id.button_done)
    lateinit var mButtonDone: AppCompatButton

    override fun getViewModelFactory(): ViewModelProvider.Factory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentView(): Int = R.layout.fragment_damage_claim_description
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initBindings()
    }

    private fun initBindings() {
        mCompositeDisposable += RxView.clicks(mButtonDone)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionDescription(mDamageClaimDescriptionEditText.text.toString())
                    loadNextFragment(Constant.FragmentTags.DAMAGE_LOCATION)
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun loadNextFragment(fragmentTag: String) {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback
        activityHolder.replaceWithFragment(fragmentTag)
    }

    private fun setMalfunctionDescription(description: String) {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback
        activityHolder.retrieveDescription(description)
    }

    override fun onFragmentStop() {

    }

    override fun resolveDaggerDependency() {

    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = DamageClaimDescriptionFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
