package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerActiveDamageClaimsListFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ActiveDamageClaimsListFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.MyCurrentLocationPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ActiveMalfunctionsListFragmentViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ActiveMalfunctionsListFragmentViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimListAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.widget.EndlessRecyclerOnScrollListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ActiveDamageClaimsListFragment : BaseFragment<ActiveMalfunctionsListFragmentViewModel>() {

    @BindView(R.id.damage_claim_list)
    lateinit var mDamageClaimList: RecyclerView

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    @Inject
    lateinit var mViewModelFactory: ActiveMalfunctionsListFragmentViewModelFactory

    @Inject
    lateinit var mImageLoader: ImageLoader

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mLocationSubject = BehaviorSubject.create<LatLng>()
    private val mAdapterItemClickSubject = BehaviorSubject.create<DamageClaim>()

    private val currentLocationPref by lazy { mAppSharedPreference.prepare<MyCurrentLocationPreference>() }

    private lateinit var mAdapter: DamageClaimListAdapter
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener

    override fun initViewModel(): ActiveMalfunctionsListFragmentViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(ActiveMalfunctionsListFragmentViewModel::class.java)
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

    override fun onFragmentReady(savedInstanceState: Bundle?) {
        mAdapter = DamageClaimListAdapter(activity, mAdapterItemClickSubject, mImageLoader)

        getViewModel().init()
        initRx(savedInstanceState)
        initRecycler()
        getCurrentLocationGps()
        recyclerStartLoadingItems()
    }

    override fun onFragmentStop() {
        SmartLocation.with(context)
                .location()
                .stop()
    }

    private fun recyclerStartLoadingItems() {
        mAdapter.addProgressFooter()
    }

    private fun getCurrentLocationGps() {
        SmartLocation.with(activity)
                .location()
                .config(LocationParams.BEST_EFFORT)
                .oneFix()
                .start {
                    mLocationSubject.onNext(LatLng(it.latitude, it.longitude))
                }
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.DAMAGE_CLAIM_ADAPTER_VIEW_WITH)
        val layoutManager = GridLayoutManager(activity, spanCount)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val type = mAdapter.getItemViewType(position)
                return when (type) {
                    AdapterItemType.VIEW_PROGRESSBAR.ordinal, AdapterItemType.VIEW_MESSAGE.ordinal -> spanCount
                    AdapterItemType.VIEW_ITEM.ordinal -> 1
                    else -> throw RuntimeException("Unknown item view type!")
                }
            }
        }

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        mDamageClaimList.layoutManager = layoutManager
        mDamageClaimList.adapter = mAdapter
        mDamageClaimList.addOnScrollListener(mEndlessScrollListener)
        mDamageClaimList.setHasFixedSize(true)
    }

    private fun initRx(savedInstanceState: Bundle?) {
        getViewModel().setIsFirstFragmentStart(savedInstanceState == null)

        mCompositeDisposable += Observables.combineLatest(mLoadMoreSubject, mLocationSubject, { loadMore, location -> Pair(loadMore, location)})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { mAdapter.addProgressFooter() }
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (page, latlon) ->
                    saveCurrentLocation(latlon)
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

        mCompositeDisposable += mAdapterItemClickSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAdapterItemClick(it) })
    }

    private fun onAdapterItemClick(damageClaim: DamageClaim) {

    }

    private fun saveCurrentLocation(location: LatLng) {
        currentLocationPref.mLocation = Fickle.of(location)
        currentLocationPref.save()
    }

    private fun getDamageClaims(page: Long, location: LatLng) {
        getViewModel().mInputs.getDamageClaimsWithinRadius(location, 75.0, page)
    }

    private fun onDamageClaimsPageReceived(damageClaimList: ArrayList<DamageClaimsWithDistanceDTO>) {
        mEndlessScrollListener.pageLoaded()

        if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mEndlessScrollListener.reachedEnd()
        }

        mAdapter.removeProgressFooter()

        val adapterDamageClaims = damageClaimList.map { AdapterItem(it, AdapterItemType.VIEW_ITEM) }
        mAdapter.addAll(adapterDamageClaims as List<AdapterItem<DamageClaimListAdapterGenericParam>>)

        if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mAdapter.addMessageFooter("Последнее объявление")
        }
    }

    override fun resolveDaggerDependency() {
        DaggerActiveDamageClaimsListFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .activeDamageClaimsListFragmentModule(ActiveDamageClaimsListFragmentModule())
                .build()
                .inject(this)
    }

    fun onShowToast(message: String, duration: Int) {
        showToast(message, duration)
    }

    fun onUnknownError(throwable: Throwable) {
        unknownError(throwable)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = ActiveDamageClaimsListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}