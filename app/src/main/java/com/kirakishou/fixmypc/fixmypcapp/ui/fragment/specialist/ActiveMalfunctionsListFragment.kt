package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerActiveDamageClaimsListFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ActiveDamageClaimsListFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.MyCurrentLocationPreference
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment.ActiveMalfunctionsListFragmentPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.ActiveDamageClaimsListFragmentView
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimListAdapter
import io.nlopez.smartlocation.SmartLocation
import javax.inject.Inject

class ActiveMalfunctionsListFragment : BaseFragment(), ActiveDamageClaimsListFragmentView {

    @BindView(R.id.damage_claim_list)
    lateinit var mDamageClaimList: RecyclerView

    @Inject
    lateinit var mPresenter: ActiveMalfunctionsListFragmentPresenterImpl

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    private lateinit var mAdapter: DamageClaimListAdapter<DamageClaimsWithDistanceDTO>
    private lateinit var currentLocationPref: MyCurrentLocationPreference

    override fun getContentView() = R.layout.fragment_active_malfunctions_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        mPresenter.initPresenter()

        mAdapter = DamageClaimListAdapter(activity)
        mAdapter.init()

        mDamageClaimList.layoutManager = LinearLayoutManager(activity)
        mDamageClaimList.adapter = mAdapter

        loadCurrentLocation()
        getDamageClaims()
    }

    private fun loadCurrentLocation() {
        currentLocationPref = mAppSharedPreference.prepare()
        currentLocationPref.load()
    }

    private fun saveCurrentLocation(latlon: LatLng) {
        currentLocationPref.mLocation = Fickle.of(latlon)
        currentLocationPref.save()
    }

    private fun getDamageClaims() {
        if (!currentLocationPref.exists()) {
            SmartLocation.with(activity)
                    .location()
                    .oneFix()
                    .start {
                        val latlon = LatLng(it.latitude, it.longitude)
                        saveCurrentLocation(latlon)

                        mPresenter.getDamageClaimsWithinRadius(latlon, 75.0, 0)
                    }
        } else {
            val latlon = currentLocationPref.mLocation.get()
            mPresenter.getDamageClaimsWithinRadius(latlon, 75.0, 0)
        }
    }

    override fun onDamageClaimsPageReceived(damageClaimList: ArrayList<DamageClaimsWithDistanceDTO>) {
        val adapterDamageClaims = damageClaimList.map { AdapterItem(it, AdapterItemType.VIEW_ITEM) }
        mAdapter.addAll(adapterDamageClaims)
    }

    override fun onFragmentStop() {
        mPresenter.destroyPresenter()

        SmartLocation.with(context)
                .location()
                .stop()
    }

    override fun resolveDaggerDependency() {
        DaggerActiveDamageClaimsListFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .activeDamageClaimsListFragmentModule(ActiveDamageClaimsListFragmentModule(this))
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