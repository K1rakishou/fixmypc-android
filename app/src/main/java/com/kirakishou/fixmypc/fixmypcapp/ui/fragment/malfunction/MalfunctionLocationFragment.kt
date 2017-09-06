package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction


import android.animation.AnimatorSet
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
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber



class MalfunctionLocationFragment : BaseFragment(), OnMapReadyCallback {

    @BindView(R.id.button_done)
    lateinit var buttonDone: AppCompatButton

    @BindView(R.id.button_zoom_in)
    lateinit var buttonZoomIn: FloatingActionButton

    @BindView(R.id.button_zoom_out)
    lateinit var buttonZoomOut: FloatingActionButton

    private lateinit var googleMap: GoogleMap

    override fun getContentView() = R.layout.fragment_malfunction_coordinates
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    private var mLocation: LatLng? = null

    override fun onFragmentReady() {
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

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
            activityHolder.retrieveLocation(mLocation!!)
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

    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = MalfunctionLocationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
