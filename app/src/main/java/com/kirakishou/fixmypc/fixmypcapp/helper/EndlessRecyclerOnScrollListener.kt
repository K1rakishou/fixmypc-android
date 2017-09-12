package com.kirakishou.fixmypc.fixmypcapp.helper

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kirakishou on 9/7/2017.
 */
class EndlessRecyclerOnScrollListener(
        private val mGridLayoutManager: GridLayoutManager,
        private val mLoadMoreSubject: BehaviorSubject<Long>) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 1
    private var firstVisibleItem = 0
    private var visibleItemCount = visibleThreshold * mGridLayoutManager.spanCount
    private var totalItemCount = 0
    private var currentPage = 0L
    private var isEndReached = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (isEndReached) {
            return
        }

        visibleItemCount = recyclerView.childCount
        totalItemCount = mGridLayoutManager.itemCount
        firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            mLoadMoreSubject.onNext(currentPage)

            currentPage++
            loading = true
        }
    }

    fun resetState() {
        firstVisibleItem = 0
        totalItemCount = 0
        currentPage = 0L
        previousTotal = 0
        isEndReached = false
    }

    fun reachedEnd() {
        isEndReached = true
    }
}