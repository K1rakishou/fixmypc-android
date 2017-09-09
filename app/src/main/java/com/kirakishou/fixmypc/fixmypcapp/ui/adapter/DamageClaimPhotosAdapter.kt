package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamagePhoto
import timber.log.Timber
import java.io.File

/**
 * Created by kirakishou on 7/31/2017.
 */
class DamageClaimPhotosAdapter(context: Context, callback: PhotoClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mLayoutInflater: LayoutInflater
    private val mItems = arrayListOf<AdapterItem<DamagePhoto>>()
    private val mCallback: PhotoClickCallback
    private val mContext: Context

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
        this.mCallback = callback
        this.mContext = context
    }

    fun add(item: AdapterItem<DamagePhoto>) {
        if (item.getType() == -1) {
            item.setType(AdapterItemType.VIEW_ADD_BUTTON)
        }

        if (item.getType() == AdapterItemType.VIEW_ADD_BUTTON.ordinal) {
            mItems.add(item)
            notifyItemInserted(mItems.size - 1)
        } else {
            mItems.add(mItems.size - 1, item)
            notifyItemInserted(mItems.size - 2)
        }

        val photosCount = mItems.size
        if (photosCount > (Constant.DAMAGE_CLAIM_PHOTO_ADAPTER_MAX_PHOTOS)) {
            //if last element of list is button
            if (mItems.last().getType() == AdapterItemType.VIEW_ADD_BUTTON.ordinal) {
                //remove it
                mItems.removeAt(photosCount - 1)
                notifyItemRemoved(mItems.size - 1)
            }
        }
    }

    fun remove(position: Int) {
        if (position < 0 || position > mItems.size) {
            return
        }

        mItems.removeAt(position)
        //notifyItemRemoved(position)

        //FIXME: for some reason recyclerview doesn't change it's size on element removing when using notifyItemRemoved.
        //For now it works with notifyDataSetChanged but the items don't have animations
        notifyDataSetChanged()

        //if we don't have a button yet
        if (mItems.last().getType() != AdapterItemType.VIEW_ADD_BUTTON.ordinal) {
            //if photosCount <= maxPhotos
            if (mItems.size <= (Constant.DAMAGE_CLAIM_PHOTO_ADAPTER_MAX_PHOTOS)) {
                //add button again
                mItems.add(AdapterItem(AdapterItemType.VIEW_ADD_BUTTON))
                notifyItemInserted(mItems.size - 1)
            }
        }
    }

    fun getPhotosCount(): Int {
        return mItems
                .filter { it.getType() == AdapterItemType.VIEW_PHOTO.ordinal }
                .count()
    }

    fun getPhotos(): ArrayList<String> {
        return ArrayList(mItems.filter { it.getType() == AdapterItemType.VIEW_PHOTO.ordinal }
                .map { it.value.get().path })
    }

    override fun getItemViewType(position: Int): Int {
        return mItems[position].getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            AdapterItemType.VIEW_ADD_BUTTON.ordinal -> {
                val view = mLayoutInflater.inflate(R.layout.adapter_photo_add_button, parent, false)
                return AddPhotoButtonViewHolder(view)
            }

            AdapterItemType.VIEW_PHOTO.ordinal -> {
                val view = mLayoutInflater.inflate(R.layout.adapter_photo_image, parent, false)
                return PhotoViewHolder(view)
            }

            else -> {
                Timber.e("Unsupported viewType: $viewType")
                throw IllegalArgumentException("Unsupported viewType: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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

                    Glide.with(mContext)
                            .load(File(photoPath.path))
                            .apply(RequestOptions()
                                    .fitCenter()
                                    .centerCrop())
                            .into(holder.imageView)

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

    override fun getItemCount() = mItems.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image_button)
        lateinit var imageButton: RelativeLayout

        @BindView(R.id.image_view)
        lateinit var imageView: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    inner class AddPhotoButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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