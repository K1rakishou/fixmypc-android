package com.kirakishou.fixmypc.fixmypcapp.module.fragment.malfunction


import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment


class MalfunctionCoordinatesFragment : BaseFragment(), OnMapReadyCallback {

    override fun getContentView() = R.layout.fragment_malfunction_coordinates
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)
    }

    override fun onFragmentStop() {

    }

    override fun onMapReady(p0: GoogleMap) {

    }

    override fun resolveDaggerDependency() {

    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = MalfunctionCoordinatesFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
