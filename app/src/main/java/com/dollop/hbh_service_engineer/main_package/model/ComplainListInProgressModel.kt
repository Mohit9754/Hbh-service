package com.dollop.hbh_service_engineer.main_package.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Complaint(

    @SerializedName("rn")
    @Expose
    var rn: String? = null,
    @SerializedName("complaintId")
    @Expose
    var complaintId: String? = null,
    @SerializedName("customerId")
    @Expose
    var customerId: String? = null,
    @SerializedName("customerName")
    @Expose
    var customerName: String? = null,
    @SerializedName("address")
    @Expose
    var address: Any? = null,
    @SerializedName("contactPerson")
    @Expose
    var contactPerson: String? = null,
    @SerializedName("contactNumber")
    @Expose
    var contactNumber: String? = null,
    @SerializedName("machineSrNo")
    @Expose
    var machineSrNo: String? = null,
    @SerializedName("modelNo")
    @Expose
    var modelNo: String? = null,
    @SerializedName("callType")
    @Expose
    var callType: String? = null,
    @SerializedName("callStatus")
    @Expose
    var callStatus: String? = null,
    @SerializedName("symptom")
    @Expose
    var symptom: String? = null,
    @SerializedName("attendanceStatus")
    @Expose
    var attendanceStatus: String? = null,
    @SerializedName("represantativeprofilePic")
    @Expose
    var represantativeprofilePic: String? = null,
    @SerializedName("createdDate")
    @Expose
    var createdDate: String? = null,
    @SerializedName("updates")
    @Expose
    var updates: List<Update>? = null
)

data class ComplainListInProgressModel(
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("complaintList")
    @Expose
    var complaintList: List<Complaint>? = null
)

data class Update(
    @SerializedName("comAttId")
    @Expose
    var comAttId: Int? = null,
    @SerializedName("checkInComment")
    @Expose
    var checkInComment: String? = null,
    @SerializedName("checkOutComment")
    @Expose
    var checkOutComment: String? = null,
    @SerializedName("attachement")
    @Expose
    var attachement: String? = null,
    @SerializedName("signature")
    @Expose
    var signature: String? = null,
    @SerializedName("location")
    @Expose
    var location: String? = null,
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null,
    @SerializedName("longitude")
    @Expose
    var longitude: String? = null,
    @SerializedName("checkIn")
    @Expose
    var checkIn: String? = null,
    @SerializedName("checkOut")
    @Expose
    var checkOut: String? = null,
    @SerializedName("callStatus")
    @Expose
    var callStatus: String? = null
)
