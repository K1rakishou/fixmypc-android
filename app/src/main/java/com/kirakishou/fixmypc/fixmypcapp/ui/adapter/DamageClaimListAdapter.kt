package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.DamageClaimsWithDistanceDTO
import timber.log.Timber

/**
 * Created by kirakishou on 9/3/2017.
 */
class DamageClaimListAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mItems = arrayListOf<AdapterItem<DamageClaimsWithDistanceDTO>>()
    private val mLayoutInflater = LayoutInflater.from(mContext)

    fun add(item: AdapterItem<DamageClaimsWithDistanceDTO>) {
        if (item.getType() != AdapterItemType.VIEW_ITEM.ordinal) {
            throw IllegalArgumentException("bad adapterItemType: ${item.getType()}")
        }

        mItems.add(item)
        notifyItemInserted(mItems.size - 1)
    }

    fun addAll(items: List<AdapterItem<DamageClaimsWithDistanceDTO>>) {
        for (item in items) {
            add(item)
        }
    }

    fun remove(position: Int) {
        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int) = mItems[position].getType()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            AdapterItemType.VIEW_ITEM.ordinal -> {
                val view = mLayoutInflater.inflate(R.layout.adapter_item_damage_claim, parent, false)
                return DamageClaimItemHolder(view)
            }

            /*AdapterItemType.DamageClaimListAdapter.VIEW_PROGRESS.ordinal -> {

            }

            AdapterItemType.DamageClaimListAdapter.VIEW_MESSAGE.ordinal -> {

            }*/

            else -> {
                Timber.e("Unsupported viewType: $viewType")
                throw IllegalArgumentException("Unsupported viewType: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DamageClaimItemHolder -> {
                val claim = mItems[position].value.get()
                val distStr = distanceToString(claim.distance)

                holder.damageCategory.text = claim.damageClaim.description
                holder.distanceToMe.text = "$distStr KM"
            }
        }
    }

    private fun distanceToString(distance: Double): String {
        if (distance < 1.0) {
            return "<1.0"
        }

        return distance.toString()
    }

    override fun getItemCount() = mItems.size

    inner class DamageClaimItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
}