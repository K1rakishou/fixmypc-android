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
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionPhoto
import timber.log.Timber
import java.io.File

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

        if (item.getType() == AdapterItemType.Photo.VIEW_ADD_BUTTON.ordinal) {
            mMalfunctionPhotos.add(item)
            notifyItemInserted(mMalfunctionPhotos.size - 1)
        } else {
            mMalfunctionPhotos.add(mMalfunctionPhotos.size - 1, item)
            notifyItemInserted(mMalfunctionPhotos.size - 2)
        }

        val photosCount = mMalfunctionPhotos.size

        //if photosCount > maxPhotos + 1 (button)
        if (photosCount > (Constant.MALFUNCTION_PHOTO_ADAPTER_MAX_PHOTOS + 1)) {
            //if last element of list is button
            if (mMalfunctionPhotos.last().getType() == AdapterItemType.Photo.VIEW_ADD_BUTTON.ordinal) {
                //remove it
                mMalfunctionPhotos.removeAt(photosCount - 1)
                notifyItemRemoved(mMalfunctionPhotos.size - 1)
            }
        }
    }

    fun remove(position: Int) {
        if (position < 0 || position > mMalfunctionPhotos.size) {
            return
        }

        mMalfunctionPhotos.removeAt(position)
        //notifyItemRemoved(position)

        //FIXME: for some reason recyclerview doesn't change it's size on element removing when using notifyItemRemoved.
        //It works with notifyDataSetChanged but without animations
        notifyDataSetChanged()

        //if we don't have a button yet
        if (mMalfunctionPhotos.last().getType() != AdapterItemType.Photo.VIEW_ADD_BUTTON.ordinal) {
            //if photosCount <= maxPhotos
            if (mMalfunctionPhotos.size <= (Constant.MALFUNCTION_PHOTO_ADAPTER_MAX_PHOTOS)) {
                //add button again
                mMalfunctionPhotos.add(AdapterItem(AdapterItemType.Photo.VIEW_ADD_BUTTON))
                notifyItemInserted(mMalfunctionPhotos.size - 1)
            }
        }
    }

    fun getPhotosCount(): Int {
        return mMalfunctionPhotos
                .filter { it.getType() == AdapterItemType.Photo.VIEW_PHOTO.ordinal }
                .count()
    }

    fun getPhotos(): ArrayList<String> {
        return ArrayList(mMalfunctionPhotos.filter { it.getType() == AdapterItemType.Photo.VIEW_PHOTO.ordinal }
                .map { it.value.get().path })
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
                val malfunctionPhoto = mMalfunctionPhotos[position].value

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