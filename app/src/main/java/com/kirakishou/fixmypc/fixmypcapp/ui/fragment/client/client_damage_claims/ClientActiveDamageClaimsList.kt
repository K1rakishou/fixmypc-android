package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.animation.LinearInterpolator
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimGeneric
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimResponseCount
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsWithCountResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.RespondedSpecialistsActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.ClientDamageClaimListAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientMainActivityNavigator
import com.kirakishou.fixmypc.fixmypcapp.ui.widget.EndlessRecyclerOnScrollListener
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ClientActiveDamageClaimsList : BaseFragment<ClientMainActivityViewModel>() {

    @BindView(R.id.new_damage_claim_button)
    lateinit var newDamageClaimButton: FloatingActionButton

    @BindView(R.id.active_damage_claim_list)
    lateinit var mActiveDamageClaimList: RecyclerView

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mNavigator: ClientMainActivityNavigator

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mAdapterItemClickSubject = BehaviorSubject.create<DamageClaim>()

    private lateinit var mAdapter: ClientDamageClaimListAdapter
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener
    private val receiver = DamageClaimRefreshCommandReceiver()

    override fun initViewModel(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_client_active_damage_claims_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()
        initRecycler()
        recyclerStartLoadingItems()

        activity.registerReceiver(receiver,
                IntentFilter(Constant.ReceiverActions.REFRESH_CLIENT_DAMAGE_CLAIMS_NOTIFICATION))
    }

    override fun onFragmentViewDestroy() {
        activity.unregisterReceiver(receiver)
        mRefWatcher.watch(this)
    }

    private fun recyclerStartLoadingItems() {
        mAdapter.addProgressFooter()
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.DAMAGE_CLAIM_ADAPTER_VIEW_WIDTH)
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

        mAdapter = ClientDamageClaimListAdapter(activity, mImageLoader, mAdapterItemClickSubject)
        mAdapter.init()

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        mActiveDamageClaimList.layoutManager = layoutManager
        mActiveDamageClaimList.adapter = mAdapter
        mActiveDamageClaimList.addOnScrollListener(mEndlessScrollListener)
        mActiveDamageClaimList.setHasFixedSize(true)
    }

    private fun initRx() {
        mCompositeDisposable += mLoadMoreSubject
                .subscribeOn(Schedulers.io())
                .subscribe({ page ->
                    mAdapter.addProgressFooter()
                    getDamageClaims(page)
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += RxView.clicks(newDamageClaimButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onNewDamageClaimButtonClick() })

        mCompositeDisposable += mAdapterItemClickSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onClientDamageClaimClick(it) })

        mCompositeDisposable += getViewModel().mOutputs.onActiveDamageClaimsResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onActiveDamageClaimsResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    private fun onNewDamageClaimButtonClick() {
        runActivity(ClientNewDamageClaimActivity::class.java)
    }

    private fun getDamageClaims(page: Long) {
        getViewModel().mInputs.getActiveClientDamageClaimSubject(page, 5)
    }

    private fun onClientDamageClaimClick(damageClaim: DamageClaim) {
        val intent = Intent(activity, RespondedSpecialistsActivity::class.java)
        val params = Bundle()
        params.putLong("damage_claim_id", damageClaim.id)

        intent.putExtras(params)
        activity.startActivity(intent)
    }

    private fun onActiveDamageClaimsResponse(response: DamageClaimsWithCountResponse) {
        mAdapter.runOnAdapterHandler {
            mEndlessScrollListener.pageLoaded()

            val damageClaimList = response.damageClaims
            val responsesCountList = response.responsesCountList

            if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
                mEndlessScrollListener.reachedEnd()
            }

            mAdapter.removeProgressFooter()

            val adapterDamageClaims = mutableListOf<AdapterItem<DamageClaimListAdapterGenericParam>>()

            for (damageClaim in damageClaimList) {
                var responsesCount = responsesCountList.firstOrNull { it.damageClaimId == damageClaim.id }
                if (responsesCount == null) {
                    responsesCount = DamageClaimResponseCount(-1, 0)
                }

                adapterDamageClaims.add(AdapterItem(DamageClaimGeneric(damageClaim, responsesCount), AdapterItemType.VIEW_ITEM))
            }

            mAdapter.addAll(adapterDamageClaims as List<AdapterItem<DamageClaimListAdapterGenericParam>>)

            if (damageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
                mAdapter.addMessageFooter("Конец списка")
            }
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
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule(activity as ClientMainActivity))
                .build()
                .inject(this)
    }

    inner class DamageClaimRefreshCommandReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constant.ReceiverActions.REFRESH_CLIENT_DAMAGE_CLAIMS_NOTIFICATION) {
                Timber.d("broadcast with action REFRESH_CLIENT_DAMAGE_CLAIMS_NOTIFICATION received")

                mAdapter.clear()
                mEndlessScrollListener.reset()
                recyclerStartLoadingItems()
            }
        }
    }
}


































