package com.dollop.hbh_service_engineer.basic.Retrofit

import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.main_package.model.AllResponseModel
import com.dollop.hbh_service_engineer.main_package.model.ComplainListInProgressModel
import com.dollop.hbh_service_engineer.main_package.model.ComplaintFollowModel
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfileModel
import com.dollop.hbh_service_engineer.main_package.model.GetCallType
import com.dollop.hbh_service_engineer.main_package.model.GetCustomerModel
import com.dollop.hbh_service_engineer.main_package.model.NotificationModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @PUT(Const.changePasswordApi)
    fun changePasswordApi(

        @Header("Authorization")  Authorization:String,
        @Field("oldPassword") oldPassword:String,
        @Field("newPassword") newPassword:String,
        @Field("confirmPassword") confirmPassword:String

    ):Call<ComplaintFollowModel>

    @GET(Const.getComplaintListApi)
    fun getComplaintListApiRecent(@Header(Constants.Authorization) token: String?,
                                      @Query(Constants.callStatus) callStatus: String?,
                                      @Query("fromDate") fromDate:String?,
                                      @Query("toDate") toDate:String?,
                                      ): Call<ComplainListInProgressModel?>?

    @GET(Const.getComplaintListApi)
    fun getComplaintListApi(@Header(Constants.Authorization) token: String?,
                            @Query(Constants.callStatus) callStatus: String?,
                            @Query("fromDate") fromDate:String?,
                            @Query("toDate") toDate:String?,
                            @Query("customerName") customerName:String?,
                            @Query("location") location:String?,
                            @Query("callType") callType:String?

    ): Call<ComplainListInProgressModel?>?

    @GET(Const.getComplaintListApi)
    fun getComplaintListApiWithComplainId(
        @Header(Constants.Authorization) token: String?,
        @Query(Constants.Complain_Id) complaintId: String?

     ): Call<ComplainListInProgressModel?>?

    @GET(Const.getCallTypeApi)
    fun getCallTypeApi(
        @Header(Constants.Authorization) token: String?

    ): Call<GetCallType?>?

    @GET(Const.getCustomerApi)
    fun getCustomerApi(
        @Header(Constants.Authorization) token: String?

    ): Call<GetCustomerModel?>?

    @FormUrlEncoded
    @POST(Const.pushNotificationApi)
    fun pushNotificationApi(
        @Header("Authorization")  Authorization:String,
        @Field("is_notification") is_notification:String
    ):Call<NotificationModel>

    @FormUrlEncoded
    @POST(Const.startJobApi)
    fun startJobApi(
        @Header("Authorization")  Authorization:String,
        @Field("complaintId") complaintId:String
    ):Call<ComplaintFollowModel>

    @FormUrlEncoded
    @POST(Const.complaintFollowupApi)
    fun complaintFollowupApiCheckIn(

        @Header("Authorization")  Authorization:String,
        @Field("complaintId") complaintId:String,
        @Field("callStatus") callStatus:String,
        @Field("punchingType") punchingType:String,
        @Field("comment") comment:String,
        @Field("location") location:String,
        @Field("latitude") latitude:String,
        @Field("longitude") longitude:String
    ):Call<ComplaintFollowModel>



    @Multipart
    @POST(Const.complaintFollowupApi)
    fun complaintFollowupApi(

        @Header("Authorization")  Authorization:String,
        @Part("complaintId") complaintId:RequestBody,
        @Part("callStatus") callStatus:RequestBody,
        @Part("punchingType") punchingType:RequestBody,
        @Part("comment") comment:RequestBody,
        @Part("location") location:RequestBody,
        @Part("latitude") latitude:RequestBody,
        @Part("longitude") longitude:RequestBody,
        @Part attachement: MultipartBody.Part,
        @Part signature: MultipartBody.Part

    ):Call<ComplaintFollowModel>

    @Multipart
    @POST(Const.updateSalesProfile)
    fun updateSalesProfile(

        @Header("Authorization")  Authorization:String,
        @Part("userName") userName:RequestBody,
        @Part("lastName") lastName:RequestBody,
        @Part profilePic: MultipartBody.Part

    ):Call<ComplaintFollowModel>

    @GET(Const.getEngineerProfileApi)
    fun getEngineerProfileApi(@Header(Constants.Authorization) token: String?): Call<EngineerProfileModel?>?

    @FormUrlEncoded
    @POST(Const.engineerLoginApi)
    fun userLogin(@FieldMap hm: HashMap<String, String?>): Call<AllResponseModel?>?

    // no use #########################################################################

    @FormUrlEncoded
    @POST(Const.loginWithOtpApi)
    fun loginWithOtp(@FieldMap hm: HashMap<String, String>): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.matchLoginOtpApi)
    fun matchLoginOtp(@FieldMap hm: HashMap<String?, String?>?): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.updateTokenApi)
    fun UpdateFirebaseToken(
        @Header(Constants.Authorization) token: String?,
        @Field(Constants.fcmId) fcmId: String?,
        @Field(Constants.device_type) deviceType: String?
    ): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.sendVarificationLinkApi)
    fun sendVarificationLinkApi(
        @Header(Constants.Authorization) Authorization: String?,
        @Field(Constants.type) type: String?
    ): Call<AllResponseModel?>?

    @GET(Const.getProfileDetailApi)
    fun getEditProfileDetails(@Header(Constants.Authorization) token: String?): Call<AllResponseModel?>?

    @GET(Const.getCountryListApi)
    fun getCountryListApi(): Call<AllResponseModel?>?

    @Multipart
    @POST(Const.updateProfileApi)
    fun EditProfileImage(
        @Header(Constants.Authorization) token: String?,
        @PartMap hm: HashMap<String?, RequestBody?>?,
        @Part profilePic: MultipartBody.Part
    ): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.socialLoginApi)
    fun SocialLogin(@FieldMap hm: HashMap<String, String?>): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.forgotPasswordApi)
    fun ForgetPassword(@FieldMap hm: HashMap<String?, String?>?): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.mobileEmailVerificationApi)
    fun mobileEmailVerificationApi(@FieldMap hm: HashMap<String, String?>): Call<AllResponseModel?>?

    @GET(Const.getPrivacyPolicyApi)
    fun getPrivacyAndPolicy(@Header(Constants.Authorization) token: String?): Call<AllResponseModel?>?

    @GET(Const.getTermConditionApi)
    fun getTermAndCondition(@Header(Constants.Authorization) token: String?): Call<AllResponseModel?>?

    @FormUrlEncoded
    @POST(Const.studentLogoutApi)
    fun studentLogOut(
        @Header(Constants.Authorization) token: String?,
        @Field(Constants.studentLoginHistoryId) studentLoginHistoryId: String?
    ): Call<AllResponseModel?>?

}