package com.dollop.hbh_service_engineer.main_package.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Customer(
    @SerializedName("customerId")
    @Expose
    val customerId: String,
    @SerializedName("customerName")
    @Expose
    val customerName: String,
    @SerializedName("customerLastName")
    @Expose
    val customerLastName: String
)

data class GetCustomerModel(
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("customer")
    @Expose
    val customer: List<Customer>
)





