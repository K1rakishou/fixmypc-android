package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.new_damage_claim


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication

import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientNewDamageClaimActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimPhotosAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class DamageClaimSendRequestFragment : BaseFragment<ClientNewDamageClaimActivityViewModel>(),
        OnMapReadyCallback {

    @BindView(R.id.damage_type)
    lateinit var mDamageType: TextView

    @BindView(R.id.damage_description)
    lateinit var mDamageDescription: TextView

    @BindView(R.id.photos)
    lateinit var mPhotosRecyclerView: RecyclerView

    @BindView(R.id.button_send)
    lateinit var mButtonSend: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mRefWatcher: RefWatcher

    lateinit var mAdapter: DamageClaimPhotosAdapter

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_damage_claim_send_request
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.damage_location) as SupportMapFragment
        mapFrag.getMapAsync(this)

        initRx()
        setUiData()
        initRecyclerView()
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initRecyclerView() {
        mAdapter = DamageClaimPhotosAdapter(activity, mImageLoader)
        mAdapter.init()

        val layoutManager = GridLayoutManager(activity,
                AndroidUtils.calculateColumnsCount(activity, Constant.Views.PHOTO_ADAPTER_VIEW_WIDTH))

        mPhotosRecyclerView.layoutManager = layoutManager
        mPhotosRecyclerView.setHasFixedSize(true)
        mPhotosRecyclerView.adapter = mAdapter
        mPhotosRecyclerView.isNestedScrollingEnabled = false

        val damageClaimInfo = getViewModel().getDamageClaimRequestInfo().damageClaimPhotos
        val mapped = damageClaimInfo.map { AdapterItem(it, AdapterItemType.VIEW_ITEM) }

        mAdapter.addAll(mapped)
    }

    private fun setUiData() {
        val damageClaimInfo = getViewModel().getDamageClaimRequestInfo()

        mDamageType.text = DamageClaimCategory.getString(damageClaimInfo.damageClaimCategory)
        mDamageDescription.text = damageClaimInfo.damageClaimDescription
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(mButtonSend)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    sendDamageClaim()
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += getViewModel().mOutputs.onMalfunctionRequestSuccessfullyCreated()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onMalfunctionRequestSuccessfullyCreated() })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.setAllGesturesEnabled(false)

        val location = getViewModel().getDamageClaimRequestInfo().damageClaimLocation
        map.addMarker(MarkerOptions().position(location))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, Constant.MAP_ZOOM))
    }

    private fun sendDamageClaim() {
        val checkWifiStatus = true //TODO get this from shared prefs

        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_SEND_REQUEST)
        getViewModel().mInputs.sendMalfunctionRequestToServer(checkWifiStatus)
    }

    private fun onMalfunctionRequestSuccessfullyCreated() {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_SEND_REQUEST)

        val intent = Intent()
        intent.action = Constant.ReceiverActions.REFRESH_CLIENT_DAMAGE_CLAIMS_NOTIFICATION

        sendBroadcast(intent)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_SEND_REQUEST)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.DAMAGE_SEND_REQUEST)

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
