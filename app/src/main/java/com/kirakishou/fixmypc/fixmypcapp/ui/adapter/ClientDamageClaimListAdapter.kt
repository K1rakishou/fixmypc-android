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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimGeneric
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimListAdapterGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimsAdapterMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kirakishou on 9/29/2017.
 */
class ClientDamageClaimListAdapter(private val mContext: Context,
                                   private val mImageLoader: ImageLoader,
                                   private val mAdapterItemClickSubject: BehaviorSubject<DamageClaim>) : BaseAdapter<DamageClaimListAdapterGenericParam>(mContext) {

    override fun add(item: AdapterItem<DamageClaimListAdapterGenericParam>) {
        checkInited()

        mItems.add(item)
        notifyItemInserted(mItems.lastIndex)
    }

    override fun addAll(items: List<AdapterItem<DamageClaimListAdapterGenericParam>>) {
        checkInited()

        for (item in items) {
            add(item)
        }
    }

    override fun remove(position: Int) {
        checkInited()

        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addProgressFooter() {
        checkInited()

        if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
            mItems.add(AdapterItem(AdapterItemType.VIEW_PROGRESSBAR))
            notifyItemInserted(mItems.lastIndex)
        }
    }

    fun removeProgressFooter() {
        checkInited()

        if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
            val index = mItems.lastIndex

            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addMessageFooter(message: String) {
        checkInited()

        if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_MESSAGE.ordinal) {
            mItems.add(AdapterItem(DamageClaimsAdapterMessage(message), AdapterItemType.VIEW_MESSAGE))
            notifyItemInserted(mItems.lastIndex)
        }
    }

    fun removeMessageFooter() {
        checkInited()

        if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_MESSAGE.ordinal) {
            val index = mItems.lastIndex

            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getBaseAdapterInfo(): MutableList<BaseAdapterInfo> {
        return mutableListOf(
                BaseAdapterInfo(AdapterItemType.VIEW_ITEM, R.layout.adapter_client_item_damage_claim,
                        DamageClaimItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_PROGRESSBAR, R.layout.item_progress,
                        ProgressBarItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_MESSAGE, R.layout.item_message,
                        MessageItemHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DamageClaimItemHolder -> {
                if (mItems[position].value.isPresent()) {
                    val item = mItems[position].value.get()
                    val damageClaim = (item as DamageClaimGeneric).damageClaim

                    holder.clickView.setOnClickListener {
                        mAdapterItemClickSubject.onNext(damageClaim)
                    }

                    when (damageClaim.category) {
                        DamageClaimCategory.Computer.ordinal -> holder.damageTypeIcon.setImageDrawable(mContext.myGetDrawable(R.drawable.ic_computer))
                        DamageClaimCategory.Notebook.ordinal -> holder.damageTypeIcon.setImageDrawable(mContext.myGetDrawable(R.drawable.ic_laptop))
                        DamageClaimCategory.Phone.ordinal -> holder.damageTypeIcon.setImageDrawable(mContext.myGetDrawable(R.drawable.ic_smartphone))
                    }

                    if (damageClaim.photoNames.isNotEmpty()) {
                        mImageLoader.loadDamageClaimImageFromNetInto(damageClaim.ownerId, damageClaim.photoNames.first(), holder.damagePhoto)
                    } else {
                        //TODO: load image with an error message
                    }
                }
            }

            is ProgressBarItemHolder -> {
                holder.progressBar.isIndeterminate = true
            }

            is MessageItemHolder -> {
                val adapterMessage = mItems[position].value.get() as DamageClaimsAdapterMessage
                holder.message.text = adapterMessage.message
            }
        }
    }

    class DamageClaimItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.click_view)
        lateinit var clickView: CardView

        @BindView(R.id.damage_photo)
        lateinit var damagePhoto: ImageView

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