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
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class MalfunctionCategoryFragment : BaseFragment() {

    @BindView(R.id.computer_category_button)
    lateinit var mComputerCategoryButton: AppCompatButton

    @BindView(R.id.notebook_category_button)
    lateinit var mNotebookCategoryButton: AppCompatButton

    @BindView(R.id.phone_category_button)
    lateinit var mPhoneCategoryButton: AppCompatButton

    override fun getContentView(): Int = R.layout.fragment_malfunction_category
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initBindings()
    }

    private fun initBindings() {
        addDisposable(RxView.clicks(mComputerCategoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    loadNextFragment(Constant.FragmentTags.MALFUNCTION_DESCRIPTION_FRAGMENT_TAG)
                }, { error ->
                    Timber.e(error)
                }))

        addDisposable(RxView.clicks(mNotebookCategoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    loadNextFragment(Constant.FragmentTags.MALFUNCTION_DESCRIPTION_FRAGMENT_TAG)
                }, { error ->
                    Timber.e(error)
                }))

        addDisposable(RxView.clicks(mPhoneCategoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    loadNextFragment(Constant.FragmentTags.MALFUNCTION_DESCRIPTION_FRAGMENT_TAG)
                }, { error ->
                    Timber.e(error)
                }))
    }

    fun loadNextFragment(fragmentTag: String) {
        val activityHolder = activity as ClientMainActivity
        activityHolder.pushFragment(fragmentTag)
    }

    override fun onFragmentStop() {

    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = MalfunctionCategoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
