package com.dollop.hbh_service_engineer.main_package.activity

import android.os.Bundle
import android.view.View
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.BaseActivity
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Attachement
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.databinding.ActivityAttachmentBinding

class Activity_Attachment : BaseActivity(), View.OnClickListener{

    private lateinit var binding: ActivityAttachmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAttachmentBinding.inflate(layoutInflater)
        init()

        setContentView(binding.root)
    }

    private fun init()
    {
//        Utils.changeStatusBarColor(this, R.color.white)
        binding.ivBackBtn.setOnClickListener(this)
        setImg()
    }

    private fun setImg() {

        val bundle = intent.extras
        val attachment:String = bundle?.getString(Attachement,Attachement).toString()
        Utils.E(attachment)
        Utils.Picasso(attachment,binding.ivAttachmentImg,R.drawable.ic_placeholder_img)
    }

    override fun onClick(view: View?) {

        if(view == binding.ivBackBtn)
        {
            super.onBackPressed()
        }
    }
}