package com.kirakishou.billboards.modules.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File

/**
 * Created by kirakishou on 4/7/2017.
 */

class PhotoView : AppCompatImageView, View.OnClickListener {
    private var isPhotoAdded = false
    private lateinit var addButtonDrawable: Drawable
    private lateinit var removeButtonDrawable: Drawable
    private lateinit var borderDrawable: Drawable
    private var addButtonId = 0
    private var removeButtonId = 0
    private var borderId = 0
    private lateinit var listener: OnPhotoClickedListener
    private lateinit var mContext: Context

    var imageFile: File? = null

    constructor(context: Context) : super(context) {
        mContext = context
        setOnClickListener(this)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        mContext = context
        setOnClickListener(this)
    }

    fun setAddButtonIcon(@DrawableRes id: Int) {
        addButtonDrawable = context.resources.getDrawable(id)
        addButtonId = id
    }

    fun setRemoveButtonIcon(@DrawableRes id: Int) {
        removeButtonDrawable = context.resources.getDrawable(id)
        removeButtonId = id
    }

    fun setBorderDrawable(@DrawableRes id: Int) {
        borderDrawable = context.resources.getDrawable(id)
        borderId = id
    }

    fun setOnPhotoClickedCallback(callback: OnPhotoClickedListener) {
        this.listener = callback
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("SUPER_INSTANCE_STATE", super.onSaveInstanceState())
        bundle.putBoolean("isPhotoAdded", isPhotoAdded)
        bundle.putInt("addButtonId", addButtonId)
        bundle.putInt("removeButtonId", removeButtonId)
        bundle.putInt("borderId", borderId)

        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        isPhotoAdded = bundle.getBoolean("isPhotoAdded", false)
        addButtonId = bundle.getInt("addButtonId", 0)
        removeButtonId = bundle.getInt("removeButtonId", 0)
        borderId = bundle.getInt("borderId", 0)
        val superState = bundle.getParcelable<Parcelable>("SUPER_INSTANCE_STATE")

        super.onRestoreInstanceState(superState)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)

        isPhotoAdded = bm != null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height

        val drawableWidth = width / 100 * 30

        val posX = (width - drawableWidth) / 2
        val posY = (height - drawableWidth) / 2

        if (!isPhotoAdded) {
            borderDrawable.setBounds(0, 0, width, height)
            borderDrawable.draw(canvas)

            addButtonDrawable.setBounds(posX, posY, posX + drawableWidth, posY + drawableWidth)
            addButtonDrawable.draw(canvas)
        } else {
            removeButtonDrawable.setBounds(posX, posY, posX + drawableWidth, posY + drawableWidth)
            removeButtonDrawable.draw(canvas)
        }
    }

    override fun onClick(v: View) {
        if (isPhotoAdded) {
            listener.removePhoto()
        } else {
            listener.addPhoto()
        }
    }

    fun loadImageFromDisk(imageFile: File) {
        Glide.with(mContext)
                .asBitmap()
                .apply(RequestOptions()
                        .centerCrop())
                .load(imageFile)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        setImageBitmap(resource)
                    }
                })
    }

    fun isPhotoAdded(): Boolean {
        return isPhotoAdded
    }

    interface OnPhotoClickedListener {
        fun addPhoto()
        fun removePhoto()
    }
}
