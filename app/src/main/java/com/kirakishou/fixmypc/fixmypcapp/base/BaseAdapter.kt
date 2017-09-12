package com.kirakishou.fixmypc.fixmypcapp.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType

/**
 * Created by kirakishou on 9/7/2017.
 */
abstract class BaseAdapter<T>(mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected lateinit var mHandler: Handler
    protected val mItems = mutableListOf<AdapterItem<T>>()
    private val mLayoutInflater = LayoutInflater.from(mContext)

    private lateinit var mBaseAdapterInfo: List<BaseAdapterInfo>

    init {
        mBaseAdapterInfo = getBaseAdapterInfo()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        mHandler = Handler(Looper.getMainLooper())
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)

        mHandler.removeCallbacksAndMessages(null)
    }

    abstract fun add(item: AdapterItem<T>)
    abstract fun addAll(items: List<AdapterItem<T>>)
    abstract fun remove(position: Int)

    open fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = mItems[position].getType()
    override fun getItemCount() = mItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        for (adapterInfo in mBaseAdapterInfo) {
            if (adapterInfo.viewType.ordinal == viewType) {
                val view = mLayoutInflater.inflate(adapterInfo.layoutId, parent, false)
                return adapterInfo.viewHolderClazz.getDeclaredConstructor(View::class.java).newInstance(view) as RecyclerView.ViewHolder
            }
        }

        throw IllegalStateException("viewType $viewType not found!")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onViewHolderBound(holder, position)
    }

    abstract fun getBaseAdapterInfo(): List<BaseAdapterInfo>
    abstract fun onViewHolderBound(holder: RecyclerView.ViewHolder, position: Int)

    inner class BaseAdapterInfo(val viewType: AdapterItemType,
                                val layoutId: Int,
                                val viewHolderClazz: Class<*>)


}