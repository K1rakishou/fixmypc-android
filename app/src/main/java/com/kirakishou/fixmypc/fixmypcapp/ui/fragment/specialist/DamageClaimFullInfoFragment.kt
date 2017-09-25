package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
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
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.ClientProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.RespondToDamageClaimResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject


class DamageClaimFullInfoFragment : BaseFragment<SpecialistMainActivityViewModel>(), OnMapReadyCallback {

    @BindView(R.id.respond_button)
    lateinit var respondButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: SpecialistMainActivityNavigator

    private val STROKE_COLOR = 0xC000A2E8.toInt()
    private val FILL_COLOR = 0x4000A2E8.toInt()
    private val MAP_ZOOM = 13.5f
    private var damageClaimFickle = Fickle.empty<DamageClaim>()

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_full_info
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.damage_claim_client_location_map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        damageClaimFickle = Fickle.of(getDamageClaimFromBundle(arguments))

        initRx()

        if (damageClaimFickle.isPresent()) {
            getViewModel().getClientProfile(damageClaimFickle.get().ownerId)
            mNavigator.showLoadingIndicatorFragment()
        } else {
            showToast("Не удалось получить данные об объявлении", Toast.LENGTH_LONG)
            mNavigator.popFragment()
        }
    }

    override fun onFragmentViewDestroy() {

    }

    private fun initRx() {
        mCompositeDisposable += getViewModel().mOutputs.onClientProfileReceived()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onClientProfileReceived(it) })

        mCompositeDisposable += RxView.clicks(respondButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onRespondButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onRespondToDamageClaimSuccessSubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onRespondToDamageClaimSuccessSubject(it)
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun onRespondButtonClick() {

    }

    private fun onRespondToDamageClaimSuccessSubject(response: RespondToDamageClaimResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onClientProfileReceived(response: ClientProfileResponse) {
        mNavigator.hideLoadingIndicatorFragment()
        Timber.e(response.clientProfile.phone)
    }

    private fun getDamageClaimFromBundle(arguments: Bundle): DamageClaim {
        val damageClaim = DamageClaim()
        damageClaim.id = arguments.getLong("damage_claim_id")
        damageClaim.ownerId = arguments.getLong("damage_claim_owner_id")
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

        map.addMarker(MarkerOptions().position(center))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, MAP_ZOOM))
    }

    override fun resolveDaggerDependency() {
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule(activity as SpecialistMainActivity))
                .build()
                .inject(this)
    }
}
