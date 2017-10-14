package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.new_damage_claim


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.CardView
import android.view.View
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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject


class DamageClaimLocationFragment : BaseFragment<ClientNewDamageClaimActivityViewModel>(),
        OnMapReadyCallback {

    @BindView(R.id.button_load_next)
    lateinit var mButtonLoadNext: AppCompatButton

    @BindView(R.id.button_zoom_in)
    lateinit var mButtonZoomIn: FloatingActionButton

    @BindView(R.id.button_zoom_out)
    lateinit var mButtonZoomOut: FloatingActionButton

    @BindView(R.id.card_view_location)
    lateinit var cardViewLocation: CardView

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private lateinit var mGoogleMap: GoogleMap

    private val MAP_ZOOM = 14f
    private var mLocation: LatLng? = null

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_coordinates
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        initRx()
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(mButtonLoadNext)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionLocation()
                    loadNextFragment()
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(mButtonZoomIn)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn())
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(mButtonZoomOut)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomOut())
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun loadNextFragment() {
        mNavigator.navigateToDamageClaimPhotosFragment()
    }

    private fun setMalfunctionLocation() {
        if (mLocation == null) {
            showToast("Текущее местоположение не задано!", Toast.LENGTH_LONG)
        } else {
            getViewModel().setLocation(mLocation!!)
        }
    }

    override fun onFragmentViewDestroy() {
        SmartLocation.with(activity)
                .location()
                .stop()

        mRefWatcher.watch(this)
    }

    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map

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
                    cardViewLocation.visibility = View.GONE
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), MAP_ZOOM))
                }
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientNewDamageClaimActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(activity as ClientNewDamageClaimActivity))
                .build()
                .inject(this)
    }
}
