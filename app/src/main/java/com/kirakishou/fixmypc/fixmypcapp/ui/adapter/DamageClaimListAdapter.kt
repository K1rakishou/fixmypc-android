package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.myGetDrawable
import com.kirakishou.fixmypc.fixmypcapp.helper.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsAdapterMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kirakishou on 9/3/2017.
 */
class DamageClaimListAdapter(private val mContext: Context,
                             private val mAdapterItemClickSubject: BehaviorSubject<DamageClaim>,
                             private val mImageLoader: ImageLoader) : BaseAdapter<DamageClaimListAdapterGenericParam>(mContext) {

    override fun add(item: AdapterItem<DamageClaimListAdapterGenericParam>) {
        checkInited()

        mHandler.post {
            mItems.add(item)
            notifyItemInserted(mItems.lastIndex)
        }
    }

    override fun addAll(items: List<AdapterItem<DamageClaimListAdapterGenericParam>>) {
        checkInited()

        for (item in items) {
            add(item)
        }
    }

    override fun remove(position: Int) {
        checkInited()

        mHandler.post {
            mItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addProgressFooter() {
        checkInited()

        mHandler.post {
            if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
                mItems.add(AdapterItem(AdapterItemType.VIEW_PROGRESSBAR))
                notifyItemInserted(mItems.lastIndex)
            }
        }
    }

    fun removeProgressFooter() {
        checkInited()

        mHandler.post {
            if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
                val index = mItems.lastIndex

                mItems.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    fun addMessageFooter(message: String) {
        checkInited()

        mHandler.post {
            if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_MESSAGE.ordinal) {
                mItems.add(AdapterItem(DamageClaimsAdapterMessage(message), AdapterItemType.VIEW_MESSAGE))
                notifyItemInserted(mItems.lastIndex)
            }
        }
    }

    fun removeMessageFooter() {
        checkInited()

        mHandler.post {
            if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_MESSAGE.ordinal) {
                val index = mItems.lastIndex

                mItems.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    override fun getBaseAdapterInfo(): MutableList<BaseAdapterInfo> {
        return mutableListOf(
                BaseAdapterInfo(AdapterItemType.VIEW_ITEM, R.layout.adapter_item_damage_claim, DamageClaimItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_PROGRESSBAR, R.layout.item_progressbar, ProgressBarItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_MESSAGE, R.layout.item_message, MessageItemHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DamageClaimItemHolder -> {
                val claim = mItems[position].value.get() as DamageClaimsWithDistanceDTO
                val distStr = Utils.distanceToString(claim.distance)

                holder.clickView.setOnClickListener {
                    mAdapterItemClickSubject.onNext(claim.damageClaim)
                }

                holder.distanceToMe.text = "$distStr КМ"

                when (claim.damageClaim.category) {
                    DamageClaimCategory.Computer.ordinal -> holder.damageTypeIcon.setImageDrawable(mContext.myGetDrawable(R.drawable.ic_computer))
                    DamageClaimCategory.Notebook.ordinal -> holder.damageTypeIcon.setImageDrawable(mContext.myGetDrawable(R.drawable.ic_laptop))
                    DamageClaimCategory.Phone.ordinal -> holder.damageTypeIcon.setImageDrawable(mContext.myGetDrawable(R.drawable.ic_smartphone))
                }

                if (claim.damageClaim.photoNames.isNotEmpty()) {
                    mImageLoader.loadImageFromNetInto(claim.damageClaim.photoNames.first(), holder.damagePhoto)
                } else {
                    //TODO: load image with an error message
                }
            }

            is ProgressBarItemHolder -> {
                holder.progressBar.isIndeterminate = true
            }

            is MessageItemHolder -> {
                val adapterMessage = mItems[position].value.get() as DamageClaimsAdapterMessage
                holder.message.text = adapterMessage.text
            }
        }
    }

    class DamageClaimItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.click_view)
        lateinit var clickView: CardView

        @BindView(R.id.damage_photo)
        lateinit var damagePhoto: ImageView

        @BindView(R.id.distance_to_me)
        lateinit var distanceToMe: TextView

        @BindView(R.id.damage_type)
        lateinit var damageTypeIcon: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class ProgressBarItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.progressbar)
        lateinit var progressBar: ProgressBar

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class MessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.message)
        lateinit var message: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}