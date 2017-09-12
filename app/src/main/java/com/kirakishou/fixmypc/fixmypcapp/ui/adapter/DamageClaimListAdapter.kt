package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
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
import com.kirakishou.fixmypc.fixmypcapp.helper.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsAdapterMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsWithDistanceDTO

/**
 * Created by kirakishou on 9/3/2017.
 */
class DamageClaimListAdapter(mContext: Context,
                             private val mImageLoader: ImageLoader) : BaseAdapter<DamageClaimListAdapterGenericParam>(mContext) {

    override fun add(item: AdapterItem<DamageClaimListAdapterGenericParam>) {
        checkIsInited()

        mHandler.post {
            mItems.add(item)
            notifyItemInserted(mItems.lastIndex)
        }
    }

    override fun addAll(items: List<AdapterItem<DamageClaimListAdapterGenericParam>>) {
        checkIsInited()

        for (item in items) {
            add(item)
        }
    }

    override fun remove(position: Int) {
        checkIsInited()

        mHandler.post {
            mItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addProgressFooter() {
        if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
            mHandler.post {
                mItems.add(AdapterItem(AdapterItemType.VIEW_PROGRESSBAR))
                notifyItemInserted(mItems.lastIndex)
            }
        }
    }

    fun removeProgressFooter() {
        if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
            mHandler.post {
                mItems.removeAt(mItems.lastIndex)
                notifyItemRemoved(mItems.lastIndex)
            }
        }
    }

    fun addMessageFooter(message: String) {
        if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_MESSAGE.ordinal) {
            mHandler.post {
                mItems.add(AdapterItem(DamageClaimsAdapterMessage(message), AdapterItemType.VIEW_MESSAGE))
                notifyItemInserted(mItems.lastIndex)
            }
        }
    }

    fun removeMessageFooter() {
        if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_MESSAGE.ordinal) {
            mHandler.post {
                mItems.removeAt(mItems.lastIndex)
                notifyItemRemoved(mItems.lastIndex)
            }
        }
    }

    override fun getBaseAdapterInfo(): List<BaseAdapterInfo> {
        return listOf(
                BaseAdapterInfo(AdapterItemType.VIEW_ITEM, R.layout.adapter_item_damage_claim, DamageClaimItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_PROGRESSBAR, R.layout.item_progressbar, ProgressBarItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_MESSAGE, R.layout.item_message, MessageItemHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DamageClaimItemHolder -> {
                val claim = mItems[position].value.get() as DamageClaimsWithDistanceDTO
                val distStr = Utils.distanceToString(claim.distance)

                holder.damageCategory.text = claim.damageClaim.description
                holder.distanceToMe.text = "$distStr КМ"

                if (claim.damageClaim.imageNamesList.isNotEmpty()) {
                    mImageLoader.loadImageFromNetInto(claim.damageClaim.imageNamesList.first(), holder.damagePhoto)
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

        @BindView(R.id.damage_photo)
        lateinit var damagePhoto: ImageView

        @BindView(R.id.damage_category)
        lateinit var damageCategory: TextView

        @BindView(R.id.distance_to_me)
        lateinit var distanceToMe: TextView

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