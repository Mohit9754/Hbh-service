package com.dollop.hbh_service_engineer.main_package.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dollop.hbh_service_engineer.main_package.fragment.Fragment_Comments

class Adapter_Comments(fa: AppCompatActivity,private val checkincomment: String, private val checkoutcomment: String): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        val fragment = when (position) {

            0 -> Fragment_Comments(checkincomment)
            1 -> Fragment_Comments(checkoutcomment)
            else -> Fragment_Comments(checkincomment)
        }
        return fragment
    }
}