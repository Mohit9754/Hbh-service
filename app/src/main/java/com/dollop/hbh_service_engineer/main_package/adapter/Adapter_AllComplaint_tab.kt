package com.dollop.hbh_service_engineer.main_package.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dollop.hbh_service_engineer.main_package.fragment.Fragment_AllComplaint
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.main_package.model.FilterData

class Adapter_AllComplaint_tab(fa: AppCompatActivity, private var filter:FilterData): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 3
    }


    override fun createFragment(position: Int): Fragment {

        val fragment = when (position)
        {

            0 -> Fragment_AllComplaint(Constants.Open,filter)
            1 -> Fragment_AllComplaint(Constants.In_Progress,filter)
            2 -> Fragment_AllComplaint(Constants.Closed,filter)
            else -> Fragment_AllComplaint(Constants.Open,filter)
        }
        return fragment
    }
}