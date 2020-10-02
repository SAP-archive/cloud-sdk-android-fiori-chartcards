package com.example.fiorichartcardsapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fiorichartcardsapp.BaseCardsFragment
import com.example.fiorichartcardsapp.ScrollableCardsFragment

const val BASECARDS_PAGE_INDEX = 0
const val SCROLLABLECARDS_PAGE_INDEX = 1

class ChartCardsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        BASECARDS_PAGE_INDEX to { BaseCardsFragment() },
        SCROLLABLECARDS_PAGE_INDEX to { ScrollableCardsFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}