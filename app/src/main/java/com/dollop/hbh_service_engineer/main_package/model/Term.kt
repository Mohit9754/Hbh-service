package com.dollop.hbh_service_engineer.main_package.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Term {
    @SerializedName("content")
    @Expose
    var content: String? = null
}