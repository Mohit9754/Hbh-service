package com.dollop.hbh_service_engineer.basic.UtilityTools

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.dollop.hbh_service_engineer.basic.Database.UserDataHelper

class AppController : Application() {
    var connectivityManager: ConnectivityManager? = null
    var connected = false

    /**
     * @return
     */
    init {
        instance = this
    }

    /**
     * @return
     */
    val isOnline: Boolean
        get() {
            try {
                connectivityManager = getContext()
                    .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                @SuppressLint("MissingPermission") val networkInfo =
                    connectivityManager!!.activeNetworkInfo
                connected = networkInfo != null && networkInfo.isAvailable &&
                        networkInfo.isConnected
                return connected
            } catch (e: Exception) {
                println("CheckConnectivity Exception: " + e.message)
                Log.v("connectivity", e.toString())
            }
            return connected
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        UserDataHelper(this)
        SavedData.instance
    }

    companion object {
        var context: Context? = null

        @get:Synchronized
        lateinit var instance: AppController
            private set

        @JvmName("getContext1")
        fun getContext(): Context {
            return instance
        }

        /**
         * @param ctx
         * @return
         */
        fun getInstance(ctx: Context): AppController {
            context = ctx.applicationContext
            return instance
        }
    }
}