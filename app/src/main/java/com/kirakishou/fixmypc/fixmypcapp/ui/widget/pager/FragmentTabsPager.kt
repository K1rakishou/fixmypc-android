package com.kirakishou.fixmypc.fixmypcapp.ui.widget.pager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims.ClientActiveDamageClaimsList
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims.ClientInactiveDamageClaimsList

/**
 * Created by kirakishou on 9/28/2017.
 */
class FragmentTabsPager(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ClientActiveDamageClaimsList()
            1 -> ClientInactiveDamageClaimsList()
            else -> throw IllegalArgumentException("No fragment for the current position $position")
        }
    }

    override fun getCount(): Int = 2
}