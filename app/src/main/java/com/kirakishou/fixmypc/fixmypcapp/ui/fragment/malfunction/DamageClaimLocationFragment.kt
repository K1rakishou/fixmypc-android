package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatButton
import android.widget.Toast
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
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientNewDamageClaimActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject


class DamageClaimLocationFragment : BaseFragment<ClientNewDamageClaimActivityViewModel>(),
        OnMapReadyCallback {

    @BindView(R.id.button_done)
    lateinit var buttonDone: AppCompatButton

    @BindView(R.id.button_zoom_in)
    lateinit var buttonZoomIn: FloatingActionButton

    @BindView(R.id.button_zoom_out)
    lateinit var buttonZoomOut: FloatingActionButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    private val MAP_ZOOM = 14f
    private lateinit var googleMap: GoogleMap
    private var mLocation: LatLng? = null

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_coordinates
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady(savedInstanceState: Bundle?) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        initRx()
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(buttonDone)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionLocation()
                    loadNextFragment()
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

    private fun loadNextFragment() {
        mNavigator.navigateToDamageClaimPhotosFragment()
    }

    private fun setMalfunctionLocation() {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback

        if (mLocation == null) {
            activityHolder.onShowToast("Текущее местоположение не задано!", Toast.LENGTH_LONG)
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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), MAP_ZOOM))
                }
    }

    override fun resolveDaggerDependency() {
        DaggerClientNewDamageClaimActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(activity as ClientNewDamageClaimActivity))
                .build()
                .inject(this)
    }
}
