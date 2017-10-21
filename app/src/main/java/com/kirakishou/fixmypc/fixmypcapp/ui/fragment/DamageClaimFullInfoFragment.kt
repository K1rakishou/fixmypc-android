package com.kirakishou.fixmypc.fixmypcapp.ui.fragment


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewPager
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
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerDamageClaimFullInfoActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.DamageClaimFullInfoActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.DamageClaimFullInfoActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.DamageClaimFullInfoActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.PhotoViewPagerAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.DamageClaimFullInfoActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject


class DamageClaimFullInfoFragment : BaseFragment<DamageClaimFullInfoActivityViewModel>(), OnMapReadyCallback {

    @BindView(R.id.view_pager)
    lateinit var viewPager: ViewPager

    @BindView(R.id.respond_button)
    lateinit var respondButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: DamageClaimFullInfoActivityNavigator

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mImageLoader: ImageLoader

    private var damageClaimFickle = Fickle.empty<DamageClaim>()
    private lateinit var photoViewPagerAdapter: PhotoViewPagerAdapter

    override fun initViewModel(): DamageClaimFullInfoActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(DamageClaimFullInfoActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_full_info
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

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

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.damage_claim_client_location_map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        damageClaimFickle = Fickle.of(getDamageClaimFromBundle(arguments))
        check(damageClaimFickle.isPresent())

        initRx()
        initPhotosViewPager()
        checkIfAlreadyResponded()
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initPhotosViewPager() {
        val damageClaim = damageClaimFickle.get()

        photoViewPagerAdapter = PhotoViewPagerAdapter(activity, damageClaim.ownerId, damageClaim.photoNames, mImageLoader)
        viewPager.adapter = photoViewPagerAdapter
        viewPager.offscreenPageLimit = 1
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(respondButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onRespondButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onRespondToDamageClaimSuccessSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onRespondToDamageClaimSuccessSubject() })

        mCompositeDisposable += getViewModel().mOutputs.onHasAlreadyRespondedResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onHasAlreadyRespondedResponse(it) })

        mCompositeDisposable += getViewModel().mOutputs.onNotifyProfileIsNotFilledIn()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onNotifyProfileIsNotFilledIn() })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    private fun onNotifyProfileIsNotFilledIn() {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
        showToast("Профиль не заполнен", Toast.LENGTH_LONG)
    }

    private fun checkIfAlreadyResponded() {
        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)

        val damageClaim = damageClaimFickle.get()
        getViewModel().mInputs.checkHasAlreadyRespondedToDamageClaim(damageClaim.id)
    }

    private fun onRespondButtonClick() {
        val damageClaim = damageClaimFickle.get()

        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
        getViewModel().mInputs.respondToDamageClaim(damageClaim.id)
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.setAllGesturesEnabled(false)

        val damageClaim = damageClaimFickle.get()
        val center = LatLng(damageClaim.lat, damageClaim.lon)

        map.addMarker(MarkerOptions().position(center))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, Constant.MAP_ZOOM))
    }

    private fun onHasAlreadyRespondedResponse(responded: Boolean) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)

        if (responded) {
            respondButton.isEnabled = false
            respondButton.text = "Заявка отправлена"
        } else {
            respondButton.isEnabled = true
            respondButton.text = "Отправить заявку"
        }
    }

    private fun onRespondToDamageClaimSuccessSubject() {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)

        respondButton.isEnabled = false
        respondButton.text = "Заявка отправлена"
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)

        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerDamageClaimFullInfoActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .damageClaimFullInfoActivityModule(DamageClaimFullInfoActivityModule(activity as DamageClaimFullInfoActivity))
                .build()
                .inject(this)
    }
}
