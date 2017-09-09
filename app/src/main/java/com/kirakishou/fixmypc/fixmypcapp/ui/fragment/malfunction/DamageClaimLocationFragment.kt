package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import butterknife.BindView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerChooseCategoryActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewMalfunctionActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject


class DamageClaimLocationFragment : BaseFragment<ClientNewMalfunctionActivityViewModel>(),
        OnMapReadyCallback {

    @BindView(R.id.button_done)
    lateinit var buttonDone: AppCompatButton

    @BindView(R.id.button_zoom_in)
    lateinit var buttonZoomIn: FloatingActionButton

    @BindView(R.id.button_zoom_out)
    lateinit var buttonZoomOut: FloatingActionButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    private lateinit var googleMap: GoogleMap

    override fun getViewModel0(): ClientNewMalfunctionActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewMalfunctionActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_coordinates
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    private var mLocation: LatLng? = null

    override fun onFragmentReady() {
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        initRx()
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(buttonDone)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionLocation()
                    loadNextFragment(Constant.FragmentTags.DAMAGE_PHOTOS)
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(buttonZoomIn)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn())
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(buttonZoomOut)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    googleMap.animateCamera(CameraUpdateFactory.zoomOut())
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun loadNextFragment(fragmentTag: String) {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback
        activityHolder.replaceWithFragment(fragmentTag)
    }

    private fun setMalfunctionLocation() {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback

        if (mLocation == null) {
            activityHolder.onShowToast("Текущее местоположение не задано!")
        } else {
            getViewModel().setLocation(mLocation!!)
        }
    }

    override fun onFragmentStop() {
        SmartLocation.with(activity)
                .location()
                .stop()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        map.setOnCameraIdleListener {
            mLocation = map.cameraPosition.target
            map.clear()
            map.addMarker(MarkerOptions().position(mLocation!!))
        }

        SmartLocation.with(activity)
                .location()
                .config(LocationParams.BEST_EFFORT)
                .oneFix()
                .start {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14f))
                }
    }

    override fun resolveDaggerDependency() {
        DaggerChooseCategoryActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule())
                .build()
                .inject(this)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = DamageClaimLocationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
