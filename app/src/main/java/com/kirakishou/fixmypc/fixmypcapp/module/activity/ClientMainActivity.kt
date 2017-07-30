package com.kirakishou.fixmypc.fixmypcapp.module.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerChooseCategoryActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ChooseCategoryActivityModule
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionCategoryFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionDescriptionFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionPhotosFragment
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ChooseCategoryActivityView



class ClientMainActivity : BaseActivity(), ChooseCategoryActivityView {

    private var isOpened: Boolean = false

    override fun getContentView() = R.layout.activity_client_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onPrepareView(savedInstanceState: Bundle?, intent: Intent) {
        super.onPrepareView(savedInstanceState, intent)

        pushFragment(Constant.FragmentTags.MALFUNCTION_CATEGORY_FRAGMENT_TAG)
    }

    fun pushFragment(fragmentTag: String) {
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = instantiateFragment(fragmentTag)
        }

        replaceFragment(fragment, fragmentTag)
    }

    private fun instantiateFragment(fragmentTag: String): Fragment {
        when (fragmentTag) {
            Constant.FragmentTags.MALFUNCTION_CATEGORY_FRAGMENT_TAG -> return MalfunctionCategoryFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_DESCRIPTION_FRAGMENT_TAG -> return MalfunctionDescriptionFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_PHOTOS_FRAGMENT_TAG -> return MalfunctionPhotosFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown fragmentTag: $fragmentTag")
        }
    }

    fun replaceFragment(fragment: Fragment, fragmentTag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit()
    }

    fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    fun sendApplication() {

    }

    override fun onViewReady() {

    }

    override fun onViewStop() {

    }

    override fun resolveDaggerDependency() {
        DaggerChooseCategoryActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .chooseCategoryActivityModule(ChooseCategoryActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    override fun onServerError(serverErrorCode: ServerErrorCode) {

    }

    override fun onUnknownError(error: Throwable) {
        showToast(error.localizedMessage, Toast.LENGTH_LONG)
    }

    override fun onBackPressed() {
        val fragmentsCount = supportFragmentManager.backStackEntryCount
        if (fragmentsCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
