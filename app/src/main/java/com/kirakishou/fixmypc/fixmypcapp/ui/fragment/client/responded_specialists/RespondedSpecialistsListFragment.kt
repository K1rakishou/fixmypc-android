package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.responded_specialists


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
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerRespondedSpecialistsActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.RespondedSpecialistsActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistProfileGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistsProfilesGeneric
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.RespondedSpecialistsViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.RespondedSpecialistsActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.RespondedSpecialistsActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.SpecialistProfileListAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.RespondedSpecialistsActivityNavigator
import com.kirakishou.fixmypc.fixmypcapp.ui.widget.EndlessRecyclerOnScrollListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class RespondedSpecialistsListFragment : BaseFragment<RespondedSpecialistsViewModel>() {

    @BindView(R.id.responded_specialists_list)
    lateinit var respondedSpecialistsList: RecyclerView

    @Inject
    lateinit var mViewModelFactory: RespondedSpecialistsActivityViewModelFactory

    @Inject
    lateinit var mNavigator: RespondedSpecialistsActivityNavigator

    @Inject
    lateinit var mImageLoader: ImageLoader

    private val mLoadMoreSubject = BehaviorSubject.create<Long>()
    private val mAdapterItemClickSubject = BehaviorSubject.create<SpecialistProfile>()
    private var mDamageClaimId = -1L

    private lateinit var mAdapter: SpecialistProfileListAdapter
    private lateinit var mEndlessScrollListener: EndlessRecyclerOnScrollListener

    override fun initViewModel(): RespondedSpecialistsViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(RespondedSpecialistsViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_responded_specialists_list
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        getDamageClaim()

        initRx()
        initRecycler()
        recyclerStartLoadingItems()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun recyclerStartLoadingItems() {
        mAdapter.addProgressFooter()
    }

    private fun initRecycler() {
        val spanCount = AndroidUtils.calculateNoOfColumns(activity, Constant.Views.SPECIALIST_PROFILE_ADAPTER_VIEW_WIDTH)
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

        mAdapter = SpecialistProfileListAdapter(activity, mImageLoader, mAdapterItemClickSubject)
        mAdapter.init()

        mEndlessScrollListener = EndlessRecyclerOnScrollListener(layoutManager, mLoadMoreSubject)

        respondedSpecialistsList.layoutManager = layoutManager
        respondedSpecialistsList.adapter = mAdapter
        respondedSpecialistsList.addOnScrollListener(mEndlessScrollListener)
        respondedSpecialistsList.setHasFixedSize(true)
    }

    private fun initRx() {
        mCompositeDisposable += mLoadMoreSubject
                .subscribeOn(Schedulers.io())
                .subscribe({ page ->
                    mAdapter.addProgressFooter()
                    getRespondedSpecialists(page)
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += mAdapterItemClickSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSpecialistProfileClick(it) })

        mCompositeDisposable += getViewModel().mOutputs.onSpecialistsListResponse()
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
        check(mDamageClaimId != -1L)

        mNavigator.navigateToSpecialistFullProfileFragment(mDamageClaimId, profile)
    }

    private fun getRespondedSpecialists(page: Long) {
        check(mDamageClaimId != -1L)

        getViewModel().mInputs.getRespondedSpecialistsSubject(mDamageClaimId, page, Constant.MAX_SPECIALISTS_PROFILES_PER_PAGE)
    }

    private fun onSpecialistsResponse(specialistsList: List<SpecialistProfile>) {
        mAdapter.runOnAdapterHandler {
            mEndlessScrollListener.pageLoaded()

            if (specialistsList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
                mEndlessScrollListener.reachedEnd()
            }

            mAdapter.removeProgressFooter()

            val adapterSpecialistProfiles = specialistsList.map { AdapterItem(SpecialistsProfilesGeneric(it), AdapterItemType.VIEW_ITEM) }
            mAdapter.addAll(adapterSpecialistProfiles as List<AdapterItem<SpecialistProfileGenericParam>>)

            if (mAdapter.itemCount == 0 && specialistsList.isEmpty()) {
                mAdapter.addMessageFooter("Пока ещё никто не откликнулся")
            } else if (mAdapter.itemCount > 0
                    && specialistsList.size < Constant.MAX_DAMAGE_CLAIMS_PER_PAGE) {
                mAdapter.addMessageFooter("Конец списка")
            }
        }
    }

    private fun getDamageClaim() {
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
        DaggerRespondedSpecialistsActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .respondedSpecialistsActivityModule(RespondedSpecialistsActivityModule(activity as RespondedSpecialistsActivity))
                .build()
                .inject(this)
    }
}























