package com.dollop.hbh_service_engineer.main_package.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("message")
    @Expose
    val message: String? = null,
    @SerializedName("notification")
    @Expose
    val notification: String? = null
)