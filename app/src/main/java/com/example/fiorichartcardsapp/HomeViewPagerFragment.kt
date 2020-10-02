package com.example.fiorichartcardsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.example.fiorichartcardsapp.adapters.ChartCardsPagerAdapter
import com.example.fiorichartcardsapp.adapters.BASECARDS_PAGE_INDEX
import com.example.fiorichartcardsapp.adapters.SCROLLABLECARDS_PAGE_INDEX
import com.example.fiorichartcardsapp.databinding.FragmentHomeViewPagerBinding

class HomeViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = ChartCardsPagerAdapter(this)

        viewPager.isUserInputEnabled = false

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            BASECARDS_PAGE_INDEX -> R.drawable.basecards_tab_selector
            SCROLLABLECARDS_PAGE_INDEX -> R.drawable.scrollablecards_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            BASECARDS_PAGE_INDEX -> getString(R.string.base_cards_title)
            SCROLLABLECARDS_PAGE_INDEX -> getString(R.string.scrollable_cards_title)
            else -> null
        }
    }
}