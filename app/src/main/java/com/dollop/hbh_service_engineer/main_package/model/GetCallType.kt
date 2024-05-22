package com.dollop.hbh_service_engineer.main_package.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CallType(
    @SerializedName("callTypeId")
    @Expose
    val callTypeId: String,
    @SerializedName("uuid")
    @Expose
    val uuid: String,
    @SerializedName("callType")
    @Expose
    val callType: String,
    @SerializedName("createdDate")
    @Expose
    val createdDate: String,
    @SerializedName("updatedDate")
    @Expose
    val updatedDate: String
)

data class GetCallType(
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("callType")
    @Expose
    val callType: List<CallType>
)
