package com.dollop.hbh_service_engineer.main_package.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.*
import com.dollop.hbh_service_engineer.databinding.ActivityDashboardBinding

class Activity_DashboardActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    @SuppressLint("ResourceAsColor")
    private fun init()
    {
        navigationSetup()

//        val bundle = intent.extras
//        val status = bundle?.getString(Redirect_Status, Redirect_Status)
//
//        if(status !=  Redirect_Status)
//        {
//            binding.bottomNavigationView
//        }
    }

    override fun onClick(view: View?)
    {

    }

    @SuppressLint("SetTextI18n")
    private fun navigationSetup() {

        val navController = findNavController(R.id.fragmentContainerView)
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}