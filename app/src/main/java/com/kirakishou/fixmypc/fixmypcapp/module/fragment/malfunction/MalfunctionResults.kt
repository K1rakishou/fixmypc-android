package com.kirakishou.fixmypc.fixmypcapp.module.fragment.malfunction

import android.animation.AnimatorSet
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment

class MalfunctionResults : BaseFragment() {

    override fun getContentView() = R.layout.fragment_malfunction_results
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {

    }

    override fun onFragmentStop() {

    }

}
