package com.dollop.hbh_service_engineer.basic.Retrofit

import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants

interface Const {
        companion object {
            //Environment  **NOTE**  Change "Live" With "Debug"  When Going Live
            const val Development = Constants.Debug

            //Live
//            const val HOST_URL = "https://sales.supportyws9.net/"

            //Debug
            const val HOST_URL = "http://sales.dollopinfotech.com/"

            const val CAMERA_REQUEST = 100
            const val GALLERY_REQUEST = 200

            const val getComplaintListApi = "getComplaintListApi"
            const val getCustomerApi = "getCustomerApi"
            const val getCallTypeApi = "getCallTypeApi"


            const val getComplaintListApiWithProgress = "getComplaintListApi?callStatus=In Progress"

            const val engineerLoginApi = "engineerLoginApi"
            const val getEngineerProfileApi = "getEngineerProfileApi"
            const val complaintFollowupApi = "complaintFollowupApi"
            const val updateSalesProfile = "updateSalesProfile"
            const val changePasswordApi = "changePasswordApi"
            const val pushNotificationApi = "pushNotificationApi"
            const val startJobApi = "startJobApi"

            //no use

            const val loginWithOtpApi = "loginWithOtpApi"
            const val matchLoginOtpApi = "matchLoginOtpApi"
            const val user = "user"
            const val getPrivacyPolicyApi = "getPrivacyPolicyApi"
            const val getProfileDetailApi = "getProfileDetailApi"
            const val updateProfileApi = "updateProfileApi"
            const val socialLoginApi = "socialLoginApi"
            const val forgotPasswordApi = "forgotPasswordApi"
            const val getTermConditionApi = "getTermConditionApi"
            const val getNotification = "getNotification"
            const val countNotification = "countNotification"
            const val updateTokenApi = "updateTokenApi"
            const val studentLogoutApi = "studentLogoutApi"
            const val sendVarificationLinkApi = "sendVarificationLinkApi"
            const val mobileEmailVerificationApi = "mobileEmailVerificationApi"
            const val getCountryListApi = "getCountryListApi"

        }
    }
