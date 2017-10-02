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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistProfileGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistsProfilesGeneric
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.SpecialistProfileListAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientMainActivityNavigator
import com.kirakishou.fixmypc.fixmypcapp.ui.widget.EndlessRecyclerOnScrollListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class RespondedSpecialistsListFragment : BaseFragment<ClientMainActivityViewModel>() {

    @BindView(R.id.responded_specialists_list)
    lateinit var respondedSpecialistsList: RecyclerView

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientMainActivityNavigator

    @Inject
    lateinit var mImageLoader: ImageLoader

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mAdapterItemClickSubject = BehaviorSubject.create<SpecialistProfile>()
    private var mDamageClaimId = -1L

    private lateinit var mSpecialistProfileAdapter: SpecialistProfileListAdapter
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener

    override fun initViewModel(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_responded_specialists_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        getDamageClaimId()

        initRx()
        initRecycler()
        recyclerStartLoadingItems()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun recyclerStartLoadingItems() {
        mSpecialistProfileAdapter.addProgressFooter()
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.SPECIALIST_PROFILE_ADAPTER_VIEW_WIDTH)
        val layoutManager = GridLayoutManager(activity, spanCount)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val type = mSpecialistProfileAdapter.getItemViewType(position)
                return when (type) {
                    AdapterItemType.VIEW_PROGRESSBAR.ordinal, AdapterItemType.VIEW_MESSAGE.ordinal -> spanCount
                    AdapterItemType.VIEW_ITEM.ordinal -> 1
                    else -> throw RuntimeException("Unknown item view type!")
                }
            }
        }

        mSpecialistProfileAdapter = SpecialistProfileListAdapter(activity, mImageLoader, mAdapterItemClickSubject)
        mSpecialistProfileAdapter.init()

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        respondedSpecialistsList.layoutManager = layoutManager
        respondedSpecialistsList.adapter = mSpecialistProfileAdapter
        respondedSpecialistsList.addOnScrollListener(mEndlessScrollListener)
        respondedSpecialistsList.setHasFixedSize(true)
    }

    private fun initRx() {
        mCompositeDisposable += mLoadMoreSubject
                .subscribeOn(Schedulers.io())
                .subscribe({ page ->
                    mSpecialistProfileAdapter.addProgressFooter()
                    getRespondedSpecialists(page)
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += mAdapterItemClickSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSpecialistProfileClick(it) })

        mCompositeDisposable += getViewModel().mOutputs.mOnSpecialistsListResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSpecialistsResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    private fun onSpecialistProfileClick(profile: SpecialistProfile) {
        mNavigator.navigateToSpecialistFullProfileFragment(profile)
    }

    private fun getRespondedSpecialists(page: Long) {
        if (mDamageClaimId == -1L) {
            throw IllegalArgumentException("mDamageClaimId == -1L")
        }

        getViewModel().mInputs.getRespondedSpecialistsSubject(mDamageClaimId, page, Constant.MAX_SPECIALISTS_PROFILES_PER_PAGE)
    }

    private fun onSpecialistsResponse(specialistsList: List<SpecialistProfile>) {
        mEndlessScrollListener.pageLoaded()

        if (specialistsList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mEndlessScrollListener.reachedEnd()
        }

        mSpecialistProfileAdapter.removeProgressFooter()

        val adapterSpecialistProfiles = specialistsList.map { AdapterItem(SpecialistsProfilesGeneric(it), AdapterItemType.VIEW_ITEM) }
        mSpecialistProfileAdapter.addAll(adapterSpecialistProfiles as List<AdapterItem<SpecialistProfileGenericParam>>)

        if (specialistsList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
            mSpecialistProfileAdapter.addMessageFooter("Конец списка")
        }
    }

    private fun getDamageClaimId() {
        mDamageClaimId = arguments.getLong("damage_claim_id", -1L)
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
}























