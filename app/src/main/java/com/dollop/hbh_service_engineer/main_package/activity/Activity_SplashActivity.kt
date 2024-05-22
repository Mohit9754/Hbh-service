package com.dollop.hbh_service_engineer.main_package.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dollop.hbh_service_engineer.basic.UtilityTools.BaseActivity
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.R

@SuppressLint("CustomSplashScreen")
class Activity_SplashActivity : BaseActivity() {

    private val activity: Activity = this@Activity_SplashActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this,R.color.blue)

        val thread  = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (Utils.IS_LOGIN()) {
                        Utils.I_clear(activity, Activity_DashboardActivity::class.java, null)
                    } else {
                        Utils.I_clear(activity, Activity_LoginActivity::class.java, null)
                    }
                }
            }
        }
        thread.start()
    }
}
