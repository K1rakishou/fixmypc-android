package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import butterknife.BindView
import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.MyCurrentLocationPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.SpecialistDamageClaimListAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import com.kirakishou.fixmypc.fixmypcapp.ui.widget.EndlessRecyclerOnScrollListener
import com.squareup.leakcanary.RefWatcher
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ActiveDamageClaimsListFragment : BaseFragment<SpecialistMainActivityViewModel>() {

    @BindView(R.id.damage_claim_list)
    lateinit var mDamageClaimList: RecyclerView

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mNavigator: SpecialistMainActivityNavigator

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mLocationSubject = BehaviorSubject.create<LatLng>()
    private val mAdapterItemClickSubject = BehaviorSubject.create<DamageClaim>()

    private val currentLocationPref by lazy { mAppSharedPreference.prepare<MyCurrentLocationPreference>() }

    private lateinit var mAdapterSpecialist: SpecialistDamageClaimListAdapter
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_active_malfunctions_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onResume() {
        super.onResume()
        currentLocationPref.load()
    }

    override fun onPause() {
        super.onPause()
        currentLocationPref.save()
    }

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        Timber.e("savedInstanceState == $savedInstanceState")

        initRx()
        initRecycler()
        getCurrentLocationGps()
        recyclerStartLoadingItems()
    }

    override fun onFragmentViewDestroy() {
        SmartLocation.with(context)
                .location()
                .stop()

        mRefWatcher.watch(this)
    }

    private fun recyclerStartLoadingItems() {
        mAdapterSpecialist.addProgressFooter()
    }

    private fun getCurrentLocationGps() {
        SmartLocation.with(activity)
                .location()
                .config(LocationParams.BEST_EFFORT)
                .oneFix()
                .start {
                    val latlon = LatLng(it.latitude, it.longitude)

                    saveCurrentLocation(latlon)
                    mLocationSubject.onNext(latlon)
                }
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.DAMAGE_CLAIM_ADAPTER_VIEW_WIDTH)
        val layoutManager = GridLayoutManager(activity, spanCount)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val type = mAdapterSpecialist.getItemViewType(position)
                return when (type) {
                    AdapterItemType.VIEW_PROGRESSBAR.ordinal, AdapterItemType.VIEW_MESSAGE.ordinal -> spanCount
                    AdapterItemType.VIEW_ITEM.ordinal -> 1
                    else -> throw RuntimeException("Unknown item view type!")
                }
            }
        }

        mAdapterSpecialist = SpecialistDamageClaimListAdapter(activity, mAdapterItemClickSubject, mImageLoader)
        mAdapterSpecialist.init()

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        mDamageClaimList.layoutManager = layoutManager
        mDamageClaimList.adapter = mAdapterSpecialist
        mDamageClaimList.addOnScrollListener(mEndlessScrollListener)
        mDamageClaimList.setHasFixedSize(true)
    }

    private fun initRx() {
        mCompositeDisposable += Observables.combineLatest(mLoadMoreSubject, mLocationSubject, { loadMore, location -> Pair(loadMore, location) })
                .subscribeOn(Schedulers.io())
                .subscribe({ (page, latlon) ->
                    mAdapterSpecialist.addProgressFooter()
                    getDamageClaims(page, latlon)
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += getViewModel().mOutputs.onDamageClaimsPageReceived()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onDamageClaimsPageReceived(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += mAdapterItemClickSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAdapterItemClick(it) })
    }

    private fun onAdapterItemClick(damageClaim: DamageClaim) {
        mNavigator.navigateToDamageClaimFullInfoFragment(damageClaim)
    }

    private fun saveCurrentLocation(location: LatLng) {
        currentLocationPref.mLocation = Fickle.of(location)
        currentLocationPref.save()
    }

    private fun getDamageClaims(page: Long, location: LatLng) {
        getViewModel().mInputs.getDamageClaimsWithinRadius(location, 75.0, page)
    }

    @Suppress("UNCHECKED_CAST")
    private fun onDamageClaimsPageReceived(damageClaimList: ArrayList<DamageClaimsWithDistanceDTO>) {
        mEndlessScrollListener.pageLoaded()

        if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mEndlessScrollListener.reachedEnd()
        }

        mAdapterSpecialist.removeProgressFooter()

        val adapterDamageClaims = damageClaimList.map { AdapterItem(it, AdapterItemType.VIEW_ITEM) }
        mAdapterSpecialist.addAll(adapterDamageClaims as List<AdapterItem<DamageClaimListAdapterGenericParam>>)

        if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mAdapterSpecialist.addMessageFooter("Последнее объявление")
        }
    }

    fun onShowToast(message: String, duration: Int) {
        showToast(message, duration)
    }

    private fun onUnknownError(throwable: Throwable) {
        unknownError(throwable)
    }

    private fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun resolveDaggerDependency() {
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule(activity as SpecialistMainActivity))
                .build()
                .inject(this)
    }
}



































