package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.DamageClaimsWithDistanceDTO

/**
 * Created by kirakishou on 9/3/2017.
 */
class DamageClaimListAdapter<T : DamageClaimsWithDistanceDTO>(val mContext: Context, mImageLoader: ImageLoader) : BaseAdapter<T>(mContext) {

    override fun getBaseAdapterInfo(): List<BaseAdapterInfo> {
        return listOf(BaseAdapterInfo(AdapterItemType.VIEW_ITEM, R.layout.adapter_item_damage_claim, DamageClaimItemHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DamageClaimItemHolder -> {
                val claim = mItems[position].value.get()
                val distStr = Utils.distanceToString(claim.distance)

                holder.damageCategory.text = claim.damageClaim.description
                holder.distanceToMe.text = "$distStr КМ"

                if (claim.damageClaim.imageNamesList.isNotEmpty()) {
                    Glide.with(mContext)
                            .load("http://kez1911.asuscomm.com:8080/v1/api/image/${claim.damageClaim.imageNamesList[0]}/${Constant.ImageSize.SMALL}/")
                            .into(holder.damagePhoto)
                }
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
}