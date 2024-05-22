package com.dollop.hbh_service_engineer.main_package.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EngineerProfile(
    @SerializedName("userId")
    @Expose
    val userId: String,
    @SerializedName("uuid")
    @Expose
    val uuid: String,
    @SerializedName("roleId")
    @Expose
    val roleId: String,
    @SerializedName("userName")
    @Expose
    val userName: String,
    @SerializedName("lastName")
    @Expose
    val lastName: String,
    @SerializedName("email")
    @Expose
    val email: String,
    @SerializedName("mobile")
    @Expose
    val mobile: String,
    @SerializedName("location")
    @Expose
    val location: String, // You can change the type to the appropriate type for 'location'
    @SerializedName("experties")
    @Expose
    val experties: String,
    @SerializedName("profilePic")
    @Expose
    val profilePic: String,
    @SerializedName("is_notification")
    @Expose
    val isNotification: String,
    @SerializedName("fcmId")
    @Expose
    val fcmId: String,
    @SerializedName("deviceType")
    @Expose
    val deviceType: String,
    @SerializedName("isActive")
    @Expose
    val isActive: String,
    @SerializedName("role")
    @Expose
    val role: String,
    @SerializedName("assigned")
    @Expose
    val assigned: String,
    @SerializedName("closed")
    @Expose
    val closed: String,
    @SerializedName("in_progress")
    @Expose
    val inProgress: String

)

data class EngineerProfileModel(
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("engineerProfile")
    @Expose
    val engineerProfile: EngineerProfile
)
