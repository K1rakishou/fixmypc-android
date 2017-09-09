package com.kirakishou.fixmypc.fixmypcapp.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType

/**
 * Created by kirakishou on 9/7/2017.
 */
abstract class BaseAdapter<T>(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mInited = false
    protected val mItems = mutableListOf<AdapterItem<T>>()
    private val mLayoutInflater = LayoutInflater.from(mContext)

    private lateinit var mBaseAdapterInfo: List<BaseAdapterInfo>

    fun init() {
        mBaseAdapterInfo = getBaseAdapterInfo()
        mInited = true
    }

    open fun add(item: AdapterItem<T>) {
        checkIsInited()

        mItems.add(item)
        notifyItemInserted(mItems.size - 1)
    }

    open fun addAll(items: List<AdapterItem<T>>) {
        checkIsInited()

        for (item in items) {
            add(item)
        }
    }

    open fun remove(position: Int) {
        checkIsInited()

        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun clear() {
        checkIsInited()

        mItems.clear()
        notifyDataSetChanged()
    }

    private fun checkIsInited() {
        if (!mInited) {
            throw IllegalStateException("You forgot to initialize base adapter with function init()")
        }
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