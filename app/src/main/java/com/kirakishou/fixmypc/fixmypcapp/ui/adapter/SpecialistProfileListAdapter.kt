package com.kirakishou.fixmypc.fixmypcapp.ui.adapter

import android.content.Context
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistProfileAdapterMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistProfileGenericParam
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.specialist_profile.SpecialistsProfilesGeneric
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kirakishou on 10/1/2017.
 */
class SpecialistProfileListAdapter(private val mContext: Context,
                                   private val mImageLoader: ImageLoader,
                                   private val mAdapterItemClickSubject: BehaviorSubject<SpecialistProfile>) : BaseAdapter<SpecialistProfileGenericParam>(mContext) {

    fun addProgressFooter() {
        checkInited()

        if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
            mItems.add(AdapterItem(AdapterItemType.VIEW_PROGRESSBAR))
            notifyItemInserted(mItems.lastIndex)
        }
    }

    fun removeProgressFooter() {
        checkInited()

        if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_PROGRESSBAR.ordinal) {
            val index = mItems.lastIndex

            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addMessageFooter(message: String) {
        checkInited()

        if (mItems.isEmpty() || mItems.last().getType() != AdapterItemType.VIEW_MESSAGE.ordinal) {
            mItems.add(AdapterItem(SpecialistProfileAdapterMessage(message), AdapterItemType.VIEW_MESSAGE))
            notifyItemInserted(mItems.lastIndex)
        }
    }

    fun removeMessageFooter() {
        checkInited()

        if (mItems.isNotEmpty() || mItems.last().getType() == AdapterItemType.VIEW_MESSAGE.ordinal) {
            val index = mItems.lastIndex

            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getBaseAdapterInfo(): MutableList<BaseAdapterInfo> {
        return mutableListOf(
                BaseAdapterInfo(AdapterItemType.VIEW_ITEM, R.layout.adapter_item_specialist_profile,
                        SpecialistProfileItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_PROGRESSBAR, R.layout.item_progress,
                        ProgressBarItemHolder::class.java),
                BaseAdapterInfo(AdapterItemType.VIEW_MESSAGE, R.layout.item_message,
                        MessageItemHolder::class.java))
    }

    override fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SpecialistProfileItemHolder -> {
                if (mItems[position].value.isPresent()) {
                    val item = mItems[position].value.get()
                    val specialistProfile = (item as SpecialistsProfilesGeneric).specialistProfile

                    holder.clickView.setOnClickListener {
                        mAdapterItemClickSubject.onNext(specialistProfile)
                    }

                    holder.profileName.text = specialistProfile.name
                    holder.profileRating.rating = specialistProfile.rating

                    val registeredOnDateStr = TimeUtils.format(specialistProfile.registeredOn)
                    if (registeredOnDateStr.isNotEmpty()) {
                        holder.profileRegisteredOn.text = "Зарегистрирован с $registeredOnDateStr"
                    } else {
                        holder.profileRegisteredOn.text = "Зарегистрирован с неизвестно"
                    }

                    if (specialistProfile.photoName.isNotEmpty()) {
                        mImageLoader.loadProfileImageFromNetInto(specialistProfile.userId, specialistProfile.photoName, holder.profilePhoto)
                    } else {
                        TODO()
                    }
                }
            }

            is ProgressBarItemHolder -> {
                holder.progressBar.isIndeterminate = true
            }

            is MessageItemHolder -> {
                val adapterMessage = mItems[position].value.get() as SpecialistProfileAdapterMessage
                holder.message.text = adapterMessage.message
            }
        }
    }

    class SpecialistProfileItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.click_view)
        lateinit var clickView: CardView

        @BindView(R.id.specialist_profile_photo)
        lateinit var profilePhoto: ImageView

        @BindView(R.id.specialist_profile_name)
        lateinit var profileName: TextView

        @BindView(R.id.specialist_profile_rating)
        lateinit var profileRating: AppCompatRatingBar

        @BindView(R.id.specialist_profile_registered_on)
        lateinit var profileRegisteredOn: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class ProgressBarItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.progressbar)
        lateinit var progressBar: ProgressBar

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class MessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.message)
        lateinit var message: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}