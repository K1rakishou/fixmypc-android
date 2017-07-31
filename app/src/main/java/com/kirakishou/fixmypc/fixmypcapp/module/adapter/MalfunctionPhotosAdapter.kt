package com.kirakishou.fixmypc.fixmypcapp.module.adapter

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
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionPhoto

/**
 * Created by kirakishou on 7/31/2017.
 */
class MalfunctionPhotosAdapter(context: Context, callback: PhotoClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mLayoutInflater: LayoutInflater
    private val mMalfunctionPhotos = arrayListOf<AdapterItem<MalfunctionPhoto>>()
    private val mCallback: PhotoClickCallback
    private val mContext: Context

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
        this.mCallback = callback
        this.mContext = context
    }

    fun add(item: AdapterItem<MalfunctionPhoto>) {
        if (item.getType() == -1) {
            item.setType(AdapterItemType.Photo.VIEW_ADD_BUTTON)
        }

        mMalfunctionPhotos.add(0, item)
        notifyItemInserted(0)
    }

    fun remove(item: AdapterItem<MalfunctionPhoto>) {
        val position = mMalfunctionPhotos.indexOf(item)
        if (position > -1) {
            mMalfunctionPhotos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mMalfunctionPhotos[position].getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            AdapterItemType.Photo.VIEW_ADD_BUTTON.ordinal -> {
                val view = mLayoutInflater.inflate(R.layout.adapter_photo_add_button, parent, false)
                return AddPhotoButtonViewHolder(view)
            }

            AdapterItemType.Photo.VIEW_PHOTO.ordinal -> {
                val view = mLayoutInflater.inflate(R.layout.adapter_photo_image, parent, false)
                return PhotoViewHolder(view)
            }

            else -> throw IllegalArgumentException("Unsupported viewType: $viewType")
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
                val malfunctionPhoto = mMalfunctionPhotos[position].value

                if (malfunctionPhoto.isPresent()) {
                    val photoPath = malfunctionPhoto.get()

                    Glide.with(mContext)
                            .load(photoPath.path)
                            .apply(RequestOptions()
                                    .fitCenter()
                                    .centerCrop())
                            .into(holder.imageView)

                    holder.imageButton.setOnClickListener { _ ->
                        mCallback.onPhotoRemoveClick(holder.adapterPosition)
                    }

                } else {
                    throw IllegalArgumentException("malfunctionPhoto does not exist!")
                }
            }

            else -> throw IllegalArgumentException("Unsupported holder: ${holder.javaClass.simpleName}")
        }
    }

    override fun getItemCount() = mMalfunctionPhotos.size

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