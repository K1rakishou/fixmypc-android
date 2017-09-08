package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
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
import com.kirakishou.fixmypc.fixmypcapp.helper.EndlessRecyclerOnScrollListener
import com.kirakishou.fixmypc.fixmypcapp.helper.annotation.RequiresViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.MyCurrentLocationPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.ActiveDamageClaimsListFragmentView
import com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.ActiveMalfunctionsListFragmentPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimListAdapter
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RequiresViewModel(ActiveMalfunctionsListFragmentPresenterImpl::class)
class ActiveDamageClaimsListFragment : BaseFragment<ActiveMalfunctionsListFragmentPresenterImpl>(), ActiveDamageClaimsListFragmentView {

    @BindView(R.id.damage_claim_list)
    lateinit var mDamageClaimList: RecyclerView

    /*@Inject
    lateinit var mPresenter: ActiveMalfunctionsListFragmentPresenterImpl*/

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mLocationSubject = BehaviorSubject.create<LatLng>()

    private lateinit var mAdapter: DamageClaimListAdapter<DamageClaimsWithDistanceDTO>
    private lateinit var currentLocationPref: MyCurrentLocationPreference
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener

    override fun getContentView() = R.layout.fragment_active_malfunctions_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        //mPresenter.initPresenter()
        //mViewModel.initPresenter()

        mAdapter = DamageClaimListAdapter(activity)
        mAdapter.init()

        initRx()
        initRecycler()
        loadCurrentLocationPrefs()
        getCurrentLocationGps()
        recyclerStartLoadingItems()
    }

    private fun recyclerStartLoadingItems() {
        mAdapter.add(AdapterItem(DamageClaimsWithDistanceDTO(1.0, DamageClaim()), AdapterItemType.VIEW_ITEM))
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

    private fun loadCurrentLocationPrefs() {
        currentLocationPref = mAppSharedPreference.prepare()
        currentLocationPref.load()
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.DAMAGE_CLAIM_ADAPTER_VIEW_WITH)
        val layoutManager = GridLayoutManager(activity, spanCount)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val type = mAdapter.getItemViewType(position)
                return when (type) {
                    AdapterItemType.VIEW_PROGRESS.ordinal, AdapterItemType.VIEW_MESSAGE.ordinal -> spanCount
                    AdapterItemType.VIEW_ITEM.ordinal -> 1
                    else -> throw RuntimeException("Unknown item view type!")
                }
            }
        }

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        mDamageClaimList.layoutManager = layoutManager
        mDamageClaimList.addOnScrollListener(mEndlessScrollListener)
        mDamageClaimList.adapter = mAdapter
        mDamageClaimList.setHasFixedSize(true)
    }

    private fun initRx() {
        mCompositeDisposable += Observables.combineLatest(mLoadMoreSubject, mLocationSubject, { loadMore, location -> Pair(loadMore, location)})
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (page, latlon) ->
                    Timber.e("Fetching next damage claims page: $page at (lat: ${latlon.latitude}, lon: ${latlon.longitude})")
                    saveCurrentLocation(latlon)
                    getDamageClaims(page, latlon)
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun saveCurrentLocation(location: LatLng) {
        currentLocationPref.mLocation = Fickle.of(location)
        currentLocationPref.save()
    }

    private fun getDamageClaims(page: Long, location: LatLng) {
        //mPresenter.getDamageClaimsWithinRadius(location, 75.0, page)
        //mViewModel.getDamageClaimsWithinRadius(location, 75.0, page)

        /*if (!currentLocationPref.exists()) {
            SmartLocation.with(activity)
                    .location()
                    .oneFix()
                    .start {
                        val location = LatLng(it.latitude, it.longitude)
                        saveCurrentLocation(location)

                        //mPresenter.getDamageClaimsWithinRadius(location, 75.0, page)

                        mLocationSubject.onNext(location)
                    }
        } else {
            //val location = currentLocationPref.mLocation.get()
            //mPresenter.getDamageClaimsWithinRadius(location, 75.0, page)
        }*/
    }

    override fun onDamageClaimsPageReceived(damageClaimList: ArrayList<DamageClaimsWithDistanceDTO>) {
        if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mEndlessScrollListener.reachedEnd()
        }

        val adapterDamageClaims = damageClaimList.map { AdapterItem(it, AdapterItemType.VIEW_ITEM) }
        mAdapter.addAll(adapterDamageClaims)
    }

    override fun onFragmentStop() {
        SmartLocation.with(context)
                .location()
                .stop()
    }

    override fun resolveDaggerDependency() {
        DaggerActiveDamageClaimsListFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .activeDamageClaimsListFragmentModule(ActiveDamageClaimsListFragmentModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message)
    }

    override fun onUnknownError(throwable: Throwable) {
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