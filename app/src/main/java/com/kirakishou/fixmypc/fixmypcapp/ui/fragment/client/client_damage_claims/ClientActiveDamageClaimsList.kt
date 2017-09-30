package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import butterknife.BindView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimListGeneric
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.ClientDamageClaimListAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.widget.EndlessRecyclerOnScrollListener
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ClientActiveDamageClaimsList : BaseFragment<ClientMainActivityViewModel>() {

    @BindView(R.id.active_damage_claim_list)
    lateinit var mActiveDamageClaimList: RecyclerView

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mImageLoader: ImageLoader

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mAdapterItemClickSubject = BehaviorSubject.create<DamageClaim>()

    private lateinit var mAdapterClient: ClientDamageClaimListAdapter
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener

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
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun recyclerStartLoadingItems() {
        mAdapterClient.addProgressFooter()
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.DAMAGE_CLAIM_ADAPTER_VIEW_WIDTH)
        val layoutManager = GridLayoutManager(activity, spanCount)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val type = mAdapterClient.getItemViewType(position)
                return when (type) {
                    AdapterItemType.VIEW_PROGRESSBAR.ordinal, AdapterItemType.VIEW_MESSAGE.ordinal -> spanCount
                    AdapterItemType.VIEW_ITEM.ordinal -> 1
                    else -> throw RuntimeException("Unknown item view type!")
                }
            }
        }

        mAdapterClient = ClientDamageClaimListAdapter(activity, mImageLoader, mAdapterItemClickSubject)
        mAdapterClient.init()

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        mActiveDamageClaimList.layoutManager = layoutManager
        mActiveDamageClaimList.adapter = mAdapterClient
        mActiveDamageClaimList.addOnScrollListener(mEndlessScrollListener)
        mActiveDamageClaimList.setHasFixedSize(true)
    }

    private fun initRx() {
        mCompositeDisposable += mLoadMoreSubject
                .subscribeOn(Schedulers.io())
                .subscribe({ page ->
                    mAdapterClient.addProgressFooter()
                    getDamageClaims(page)
                }, { error ->
                    Timber.e(error)
                })

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

    private fun getDamageClaims(page: Long) {
        getViewModel().mInputs.getActiveClientDamageClaimSubject(page,  5)
    }

    private fun onClientDamageClaimClick(damageClaim: DamageClaim) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onActiveDamageClaimsResponse(activeDamageClaimList: MutableList<DamageClaim>) {
        mEndlessScrollListener.pageLoaded()

        if (activeDamageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mEndlessScrollListener.reachedEnd()
        }

        mAdapterClient.removeProgressFooter()

        val adapterDamageClaims = activeDamageClaimList.map { AdapterItem(DamageClaimListGeneric(it), AdapterItemType.VIEW_ITEM) }
        mAdapterClient.addAll(adapterDamageClaims as List<AdapterItem<DamageClaimListAdapterGenericParam>>)

        if (activeDamageClaimList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mAdapterClient.addMessageFooter("Последнее объявление")
        }
    }

    private fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    private fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule(activity as ClientMainActivity))
                .build()
                .inject(this)
    }
}


































