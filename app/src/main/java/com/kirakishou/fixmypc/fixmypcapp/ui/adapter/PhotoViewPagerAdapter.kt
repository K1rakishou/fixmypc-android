package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 10/16/2017.
 */
class PhotoViewPagerAdapter(private val context: Context,
                            private val userId: Long,
                            private val photosList: List<String>,
                            private val imageLoader: ImageLoader) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        container.addView(imageView, 0)

        imageLoader.loadDamageClaimImageFromNetInto(userId, photosList[position], imageView)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as ImageView)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj

    override fun getCount(): Int = photosList.size
}