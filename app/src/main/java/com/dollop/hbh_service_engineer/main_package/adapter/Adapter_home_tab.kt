package com.dollop.hbh_service_engineer.main_package.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.main_package.fragment.Fragment_RecentComplaints

class Adapter_home_tab(fa: AppCompatActivity): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        val fragment = when (position) {
            0 -> Fragment_RecentComplaints(Constants.Today_Assigned)
            1 -> Fragment_RecentComplaints(Constants.In_Progress)
            else -> Fragment_RecentComplaints(Constants.Today_Assigned)
        }
        return fragment
    }
}