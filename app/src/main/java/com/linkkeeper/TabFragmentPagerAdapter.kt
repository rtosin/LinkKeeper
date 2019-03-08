package com.linkkeeper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

private const val TAB_COUNT = 2
//private val tabTitles = arrayOf("NEW", "READ", "ARCHIVE")
private val tabTitles = arrayOf("NEW", "READ")

class TabFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return NewTabFragment.newInstance()
            1 -> return ReadTabFragment.newInstance()
            //2 -> return ArchiveTabFragment.newInstance()
            else -> return NewTabFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return TAB_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles.get(position)
    }

}