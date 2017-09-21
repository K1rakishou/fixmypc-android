package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamagePhotoDTO
import timber.log.Timber
import java.io.File

/**
 * Created by kirakishou on 7/31/2017.
 */
class DamageClaimPhotosAdapter(context: Context,
                               callback: PhotoClickCallback,
                               private val mImageLoader: ImageLoader) : BaseAdapter<DamagePhotoDTO>(context) {

    private val mCallback: PhotoClickCallback
    private val mContext: Context

    init {
        this.mCallback = callback
        this.mContext = context
    }

    override fun add(item: AdapterItem<DamagePhotoDTO>) {
        checkInited()

        mHandler.post {
            if (item.getType() == -1) {
                item.setType(AdapterItemType.VIEW_ADD_BUTTON)
            }

            if (item.getType() == AdapterItemType.VIEW_ADD_BUTTON.ordinal) {
                mItems.add(item)
                //notifyItemInserted(mItems.lastIndex)
                notifyDataSetChanged()
            } else {
                mItems.add(mItems.lastIndex, item)
                notifyItemInserted(mItems.lastIndex - 1)
            }

            if (mItems.size > (Constant.DAMAGE_CLAIM_PHOTO_ADAPTER_MAX_PHOTOS)) {
                //if last element of list is button
                if (mItems.last().getType() == AdapterItemType.VIEW_ADD_BUTTON.ordinal) {
                    //remove it
                    mItems.removeAt(mItems.lastIndex)
                    notifyItemRemoved(mItems.lastIndex)
                }
            }
        }
    }

    override fun addAll(items: List<AdapterItem<DamagePhotoDTO>>) {
        checkInited()

        for (item in items) {
            add(item)
        }
    }

    override fun remove(position: Int) {
        checkInited()

        if (position < 0 || position > mItems.size) {
            return
        }

        mHandler.post {
            mItems.removeAt(position)

            //FIXME: for some reason recyclerview doesn't change it's size on element removing when using notifyItemRemoved.
            //For now it works with notifyDataSetChanged but the items don't have animations
            //notifyItemRemoved(position)
            notifyDataSetChanged()

            //if we don't have a button yet
            if (mItems.last().getType() != AdapterItemType.VIEW_ADD_BUTTON.ordinal) {
                //if photosCount <= maxPhotos
                if (mItems.size <= (Constant.DAMAGE_CLAIM_PHOTO_ADAPTER_MAX_PHOTOS)) {
                    //add button again
                    mItems.add(AdapterItem(AdapterItemType.VIEW_ADD_BUTTON))
                    notifyItemInserted(mItems.lastIndex)
                }
            }
        }
    }

    fun getPhotosCount(): Int {
        checkInited()

        return mItems
                .filter { it.getType() == AdapterItemType.VIEW_PHOTO.ordinal }
                .count()
    }

    fun getPhotos(): ArrayList<String> {
        checkInited()

        return ArrayList(mItems.filter { it.getType() == AdapterItemType.VIEW_PHOTO.ordinal }
                .map { it.value.get().path })
    }

    override fun getBaseAdapterInfo(): MutableList<BaseAdapterInfo> {
        return mutableListOf(
                BaseAdapterInfo(AdapterItemType.VIEW_ADD_BUTTON, R.layout.adapter_photo_add_button, AddPhotoButtonViewHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_PHOTO, R.layout.adapter_photo_image, PhotoViewHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddPhotoButtonViewHolder -> {
                holder.ll.setOnClickListener { _ ->
                    mCallback.onPhotoAddClick(holder.adapterPosition)
                }
            }

            is PhotoViewHolder -> {
                val malfunctionPhoto = mItems[position].value

                if (malfunctionPhoto.isPresent()) {
                    val photoPath = malfunctionPhoto.get()
                    mImageLoader.loadImageFromDiskInto(File(photoPath.path), holder.imageView)

                    holder.imageButton.setOnClickListener { _ ->
                        mCallback.onPhotoRemoveClick(holder.adapterPosition)
                    }

                } else {
                    Timber.e("malfunctionPhoto does not exist!")
                    throw IllegalArgumentException("malfunctionPhoto does not exist!")
                }
            }

            else -> {
                Timber.e("Unsupported holder: ${holder.javaClass.simpleName}")
                throw IllegalArgumentException("Unsupported holder: ${holder.javaClass.simpleName}")
            }
        }
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image_button)
        lateinit var imageButton: RelativeLayout

        @BindView(R.id.image_view)
        lateinit var imageView: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class AddPhotoButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.add_photo_button)
        lateinit var ll: LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    interface PhotoClickCallback {
        fun onPhotoAddClick(position: Int)
        fun onPhotoRemoveClick(position: Int)
    }
}