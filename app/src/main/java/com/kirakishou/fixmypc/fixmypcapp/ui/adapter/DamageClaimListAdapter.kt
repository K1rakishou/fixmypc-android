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
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaim
import timber.log.Timber

/**
 * Created by kirakishou on 9/3/2017.
 */
class DamageClaimListAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mItems = arrayListOf<AdapterItem<DamageClaim>>()
    private val mLayoutInflater = LayoutInflater.from(mContext)

    fun add(item: AdapterItem<DamageClaim>) {
        mItems.add(item)
        notifyItemInserted(mItems.size - 1)
    }

    fun remove(position: Int) {
        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int) = mItems[position].getType()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            AdapterItemType.DamageClaimListAdapter.VIEW_ITEM.ordinal -> {
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
                val damageClaim = mItems[position].value.get()

                holder.damageCategory.text = damageClaim.description
            }
        }
    }

    override fun getItemCount() = mItems.size

    inner class DamageClaimItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.damage_photo)
        lateinit var damagePhoto: ImageView

        @BindView(R.id.damage_category)
        lateinit var damageCategory: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}