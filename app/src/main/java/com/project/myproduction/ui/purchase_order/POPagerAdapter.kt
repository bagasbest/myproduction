package com.project.myproduction.ui.purchase_order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.project.myproduction.ui.purchase_order.option.FormulatedFragment
import com.project.myproduction.ui.purchase_order.option.HerbFragment

class POPagerAdapter(
    supportFragmentManager: FragmentManager,
    private val tabCount: Int
) : FragmentPagerAdapter(supportFragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                HerbFragment()
            }
            1 -> {
                FormulatedFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return tabCount
    }


}