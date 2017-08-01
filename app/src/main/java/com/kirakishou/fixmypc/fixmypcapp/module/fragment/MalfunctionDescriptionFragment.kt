package com.kirakishou.fixmypc.fixmypcapp.module.fragment

import android.animation.AnimatorSet
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.CardView
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.module.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber


class MalfunctionDescriptionFragment : BaseFragment() {

    @BindView(R.id.card_view)
    lateinit var mViewHolderCardView: CardView

    @BindView(R.id.malfunction_description)
    lateinit var mMalfunctionDescriptionEditText: TextInputEditText

    @BindView(R.id.button_done)
    lateinit var mButtonDone: AppCompatButton

    override fun getContentView(): Int = R.layout.fragment_malfunction_description
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initBindings()
    }

    private fun initBindings() {
        addDisposable(RxTextView.textChanges(mMalfunctionDescriptionEditText)
                .skipInitialValue()
                .subscribeOn(AndroidSchedulers.mainThread())
                .map { text ->
                    return@map text.isEmpty()
                }
                .distinctUntilChanged()
                .subscribe({ isEmpty ->
                    Timber.e("isEmpty: $isEmpty")
                    mButtonDone.isEnabled = !isEmpty
                }, { error ->
                    Timber.e(error)
                }))

        addDisposable(RxView.clicks(mButtonDone)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionDescription(mMalfunctionDescriptionEditText.text.toString())
                    loadNextFragment(Constant.FragmentTags.MALFUNCTION_PHOTOS_FRAGMENT_TAG)
                }, { error ->
                    Timber.e(error)
                }))
    }

    private fun loadNextFragment(fragmentTag: String) {
        val activityHolder = activity as ClientMainActivity
        activityHolder.pushFragment(fragmentTag)
    }

    private fun setMalfunctionDescription(description: String) {
        val activityHolder = activity as ClientMainActivity
        activityHolder.setMalfunctionDescription(description)
    }

    override fun onFragmentStop() {

    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = MalfunctionDescriptionFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
