package com.kirakishou.fixmypc.fixmypcapp.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kirakishou.fixmypc.fixmypcapp.R


class LoadingIndicatorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_loading_indicator, container, false)
    }
}
