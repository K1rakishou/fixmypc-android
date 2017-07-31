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
import com.kirakishou.fixmypc.fixmypcapp.manager.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionCategoryFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionDescriptionFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionPhotosFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.MalfunctionPhotosFragmentCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.ClientMainActivityPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ChooseCategoryActivityView
import javax.inject.Inject


class ClientMainActivity : BaseActivity(), ChooseCategoryActivityView {

    @Inject
    lateinit var mPresenter: ClientMainActivityPresenterImpl

    @Inject
    lateinit var mPermissionManager: PermissionManager

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

    fun popFragment() {
        supportFragmentManager.popBackStack()
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

    fun requestPermission(permission: String, requestCode: Int) {
        mPermissionManager.askForPermission(this, permission, requestCode) { granted ->
            if (granted) {
                val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
                val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)

                if (currentFragment is MalfunctionPhotosFragmentCallbacks) {
                    currentFragment.onPermissionGranted()
                }

            } else {
                showErrorMessageDialog("Не удалось получить разрешение на открытие галлереи фото")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun sendApplicationToServer() {
        mPresenter.sendApplicationToServer()
    }

    override fun onViewReady() {
        mPresenter.initPresenter()
    }

    override fun onViewStop() {
        mPresenter.destroyPresenter()
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
