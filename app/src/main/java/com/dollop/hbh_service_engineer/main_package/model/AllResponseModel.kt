package com.dollop.hbh_service_engineer.main_package.model

import android.database.sqlite.SQLiteDatabase
import com.dollop.hbh_service_engineer.basic.Database.UserData
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class EngineerInfo(
    @SerializedName("userId")
    @Expose
    var userId: String? = null,
    @SerializedName("uuid")
    @Expose
    var uuid: String? = null,
    @SerializedName("roleId")
    @Expose
    var roleId: String? = null,
    @SerializedName("userName")
    @Expose
    var userName: String? = null,
    @SerializedName("lastName")
    @Expose
    var lastName: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("location")
    @Expose
    var location: String? = null,
    @SerializedName("experties")
    @Expose
    var experties: String? = null,
    @SerializedName("profilePic")
    @Expose
    var profilePic: String? = null,
    @SerializedName("is_notification")
    @Expose
    var isNotification: String? = null,
    @SerializedName("fcmId")
    @Expose
    var fcmId: String? = null,
    @SerializedName("deviceType")
    @Expose
    var deviceType: String? = null,
    @SerializedName("isActive")
    @Expose
    var isActive: String? = null,
    @SerializedName("role")
    @Expose
    var role: String? = null,
    @SerializedName("assigned")
    @Expose
    var assigned: String? = null,
    @SerializedName("closed")
    @Expose
    var closed: String? = null,
    @SerializedName("in_progress")
    @Expose
    var inProgress: String? = null,
    @SerializedName("token")
    @Expose
    var token: String? = null
)

 class AllResponseModel {
     @SerializedName("message")
     @Expose
     var message: String? = null

     @SerializedName("engineerInfo")
     @Expose
     var engineerInfo: EngineerInfo? = null

     companion object {

         const val TABLE_NAME = "examoneOone"

         //    All Key
         const val KEY__ID = "_id"
         const val KEY_ID = "userId"
         const val KEY_Uuid = "uuid"
         const val KEY_RoleId = "roleId"
         const val KEY_UserName = "userName"
         const val KEY_LastName = "lastName"
         const val KEY_Email = "email"
         const val KEY_Mobile = "mobile"
         const val KEY_Location = "location"
         const val KEY_Experties = "experties"
         const val KEY_ProfilePic = "profilePic"
         const val KEY_IsNotification = "isNotification"
         const val KEY_FcmId = "fcmId"
         const val KEY_DeviceType = "deviceType"
         const val KEY_IsActive = "isActive"
         const val KEY_Role = "role"
         const val KEY_Assigned = "assigned"
         const val KEY_Closed = "closed"
         const val KEY_InProgress = "inProgress"
         const val KEY_Token = "token"

         @JvmStatic
         fun CreateTable(db: SQLiteDatabase) {
             val CreateTableQuery = ("create table " + TABLE_NAME + " ("
                     +  KEY__ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                     + KEY_ID + " text ," +
                     KEY_Uuid + " text," +
                     KEY_RoleId + " text," +
                     KEY_UserName + " text," +
                     KEY_LastName + " text," +
                     KEY_Email + " text," +
                     KEY_Mobile + " text," +
                     KEY_Location + " text," +
                     KEY_Experties + " text," +
                     KEY_ProfilePic + " text," +
                     KEY_IsNotification + " text," +
                     KEY_FcmId + " text," +
                     KEY_DeviceType + " text," +
                     KEY_IsActive + " text," +
                     KEY_Role + " text," +
                     KEY_Assigned + " text," +
                     KEY_Closed + " text," +
                     KEY_InProgress + " text," +
                     KEY_Token + " text" +

                     " )")

             Utils.E("CreateTableQuery::$CreateTableQuery")
             db.execSQL(CreateTableQuery)
         }

         /**
          * @param db SQLiteDatabase
          */
         @JvmStatic
         fun dropTable(db: SQLiteDatabase) {
             db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
         }


     }
 }