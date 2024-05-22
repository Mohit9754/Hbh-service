package com.dollop.hbh_service_engineer.main_package.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ComplaintFollowModel {

    @SerializedName("message")
    @Expose
    var message: String? = null
}