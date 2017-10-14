package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import java.io.File

/**
 * Created by kirakishou on 10/14/2017.
 */
class DamageClaimPhotosAdapter(private val mContext: Context,
                               private val mImageLoader: ImageLoader) : BaseAdapter<String>(mContext) {

    override fun getBaseAdapterInfo(): MutableList<BaseAdapterInfo> {
        return mutableListOf(BaseAdapterInfo(AdapterItemType.VIEW_ITEM, R.layout.adapter_item_image,
                PhotoItemHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhotoItemHolder -> {
                if (mItems[position].value.isPresent()) {
                    val photoPath = mItems[position].value.get()
                    mImageLoader.loadImageFromDiskInto(File(photoPath), holder.photo)
                }
            }
        }
    }

    class PhotoItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image_view)
        lateinit var photo: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}