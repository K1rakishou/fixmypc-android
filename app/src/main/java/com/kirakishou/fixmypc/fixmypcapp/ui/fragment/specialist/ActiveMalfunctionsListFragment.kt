package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerActiveMalfunctionsListFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ActiveMalfunctionsListFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment.ActiveMalfunctionsListFragmentPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.ActiveMalfunctionsListFragmentView
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimListAdapter
import javax.inject.Inject

class ActiveMalfunctionsListFragment : BaseFragment(), ActiveMalfunctionsListFragmentView {

    @BindView(R.id.damage_claim_list)
    lateinit var mDamageClaimList: RecyclerView

    @Inject
    lateinit var mPresenter: ActiveMalfunctionsListFragmentPresenterImpl

    private lateinit var mAdapter: DamageClaimListAdapter

    override fun getContentView() = R.layout.fragment_active_malfunctions_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        mPresenter.initPresenter()

        mAdapter = DamageClaimListAdapter(activity)
        mAdapter.setHasStableIds(true)
        mDamageClaimList.layoutManager = LinearLayoutManager(activity)
        mDamageClaimList.adapter = mAdapter

        mPresenter.getDamageClaims(0)
    }

    override fun onFragmentStop() {
        mPresenter.destroyPresenter()
    }

    override fun resolveDaggerDependency() {
        DaggerActiveMalfunctionsListFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .activeMalfunctionsListFragmentModule(ActiveMalfunctionsListFragmentModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message)
    }

    override fun onUnknownError(throwable: Throwable) {
        unknownError(throwable)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = ActiveMalfunctionsListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}