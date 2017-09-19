package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerActiveDamageClaimsListFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ActiveDamageClaimsListFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ActiveDamageClaimListFragmentViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ActiveDamageClaimListFragmentViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import javax.inject.Inject


class DamageClaimFullInfoFragment : BaseFragment<ActiveDamageClaimListFragmentViewModel>(), OnMapReadyCallback {

    @Inject
    lateinit var mViewModelFactory: ActiveDamageClaimListFragmentViewModelFactory

    private val STROKE_COLOR = 0xC000A2E8.toInt()
    private val FILL_COLOR = 0x4000A2E8.toInt()
    private val MAP_ZOOM = 5f
    private var damageClaimFickle = Fickle.empty<DamageClaim>()

    override fun initViewModel(): ActiveDamageClaimListFragmentViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(ActiveDamageClaimListFragmentViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_full_info
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.damage_claim_client_location_map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        damageClaimFickle = Fickle.of(getDamageClaimFromBundle(arguments))
    }

    override fun onFragmentViewDestroy() {

    }

    private fun getDamageClaimFromBundle(arguments: Bundle): DamageClaim {
        val damageClaim = DamageClaim()
        damageClaim.id = arguments.getLong("damage_claim_id")
        damageClaim.isActive = arguments.getBoolean("damage_claim_is_active")
        damageClaim.category = arguments.getInt("damage_claim_category")
        damageClaim.description = arguments.getString("damage_claim_description")
        damageClaim.lat = arguments.getDouble("damage_claim_lat")
        damageClaim.lon = arguments.getDouble("damage_claim_lon")
        damageClaim.createdOn = arguments.getLong("damage_claim_created_on")
        damageClaim.photoNames = arguments.getStringArrayList("damage_claim_photo_names")

        return damageClaim
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.setAllGesturesEnabled(false)

        val damageClaim = damageClaimFickle.get()
        val center = LatLng(damageClaim.lat, damageClaim.lon)
        val radius = 75.0 * 1000

        val bounds = LatLngBounds.Builder()
                .include(SphericalUtil.computeOffset(center, radius, 0.0))
                .include(SphericalUtil.computeOffset(center, radius, 90.0))
                .include(SphericalUtil.computeOffset(center, radius, 180.0))
                .include(SphericalUtil.computeOffset(center, radius, 270.0))
                .build()

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15))
        map.addCircle(CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(STROKE_COLOR)
                .fillColor(FILL_COLOR)
                .strokeWidth(3f))

    }

    override fun resolveDaggerDependency() {
        DaggerActiveDamageClaimsListFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .activeDamageClaimsListFragmentModule(ActiveDamageClaimsListFragmentModule(activity as SpecialistMainActivity))
                .build()
                .inject(this)
    }
}
