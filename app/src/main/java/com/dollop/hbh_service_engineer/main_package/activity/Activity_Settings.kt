package com.dollop.hbh_service_engineer.main_package.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Database.UserDataHelper
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.I
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.changeStatusBarColor
import com.dollop.hbh_service_engineer.databinding.ActivitySettingsBinding
import com.dollop.hbh_service_engineer.main_package.model.NotificationModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class Activity_Settings : AppCompatActivity(),View.OnClickListener {

    private lateinit var binding: ActivitySettingsBinding
    private val activity = this@Activity_Settings

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        init()
    }

    private fun init()
    {
        changeStatusBarColor(this, R.color.fragment_bg)
        binding.ivBack.setOnClickListener(this)
        binding.rlChangePass.setOnClickListener(this)
        setSwitchLis()
        setSwitch()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun setSwitch()
    {
        val notification =  Utils.GetSession().isNotification.toString()
        binding.switchNotification.isChecked = notification == "1"
    }

    private fun updateNotificatonStatus(notification:String)
    {
//        Utils.T(this,notification)
        val data = UserDataHelper(this)
        data.updateNotification(notification)
//        setSwitch()
    }

    private fun notificationApi(is_notification:String)
    {
        val progressDialog = Utils.initProgressDialog(this)

        RetrofitClient.client.pushNotificationApi(Utils.GetSession().token!!,is_notification).enqueue(object : retrofit2.Callback<NotificationModel?> {
            override fun onResponse(
                call: Call<NotificationModel?>,
                response: Response<NotificationModel?>
            ) {
                progressDialog.dismiss()
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val data = response.body()
                        updateNotificatonStatus(data!!.notification.toString())

                    } else {
                        if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(activity, message.message)
                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(activity, message.message)
                            Utils.UnAuthorizationToken(activity)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<NotificationModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })
    }

    private fun setSwitchLis()
    {
        binding.switchNotification.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->

            if (isChecked)
            {
                notificationApi("1")

            } else
            {
                notificationApi("0")
            }
        })
    }

    override fun onClick(view: View?) {

        if(view ==  binding.ivBack)
        {
            super.onBackPressed()
        }
        else if(view == binding.rlChangePass)
        {
            I(this,Activity_ChangePassword::class.java,null)
        }
    }
}