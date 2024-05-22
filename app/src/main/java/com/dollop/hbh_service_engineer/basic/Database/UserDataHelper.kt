package com.dollop.hbh_service_engineer.basic.Database


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.E
import com.dollop.hbh_service_engineer.main_package.model.AllResponseModel
import com.dollop.hbh_service_engineer.main_package.model.EngineerInfo
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfile

class UserDataHelper(cx: Context) {
    private val dm: DataManager
    private var db: SQLiteDatabase? = null

    /**
     * EngineerInfoHelper constructor
     *
     * @param cx //
     */
    init {
        instance = this
        dm = DataManager(cx, DataManager.DATABASE_NAME, null, DataManager.DATABASE_VERSION)
    }

    /**
     * Open database
     */
    fun open() {
        db = dm.writableDatabase
    }

    /**
     * Close database
     */
    fun close() {
        // db.close()
    }

    /**
     * Read database
     */
    fun read() {
        db = dm.readableDatabase
    }

    /**
     * Delete by user id from the table
     *
     * @param engineerInfo //
     */
    fun delete(engineerInfo: EngineerInfo) {
        open()
        db!!.delete(
            AllResponseModel.TABLE_NAME, AllResponseModel.KEY_ID + " = '"
                    + engineerInfo.userId + "'", null
        )
        close()
    }

    /**
     * Delete All Data From the Table
     */
    fun deleteAll() {
        open()
        db!!.delete(AllResponseModel.TABLE_NAME, null, null)
        close()
    }

    /**
     * Check if data exists in the table
     *
     * @param engineerInfo //
     * @return //
     */
    private fun isExist(engineerInfo: EngineerInfo): Boolean {
        read()
        @SuppressLint("Recycle") val cur = db!!.rawQuery(
            "select * from " + AllResponseModel.TABLE_NAME + " where " + AllResponseModel.KEY_ID + "='"
                    + engineerInfo.userId + "'", null
        )
        return cur.moveToFirst()
    }

    /**
     * Insert Data in the table
     *
     * @param engineerInfo //
     */
    fun insertData(engineerInfo: EngineerInfo) {
        open()
        val values = ContentValues()
        values.put(AllResponseModel.KEY_ID, engineerInfo.userId)
        values.put(AllResponseModel.KEY_Uuid, engineerInfo.uuid)
        values.put(AllResponseModel.KEY_RoleId, engineerInfo.roleId)
        values.put(AllResponseModel.KEY_UserName, engineerInfo.userName)
        values.put(AllResponseModel.KEY_LastName, engineerInfo.lastName)
        values.put(AllResponseModel.KEY_Email, engineerInfo.email)
        values.put(AllResponseModel.KEY_Mobile, engineerInfo.mobile)
        values.put(AllResponseModel.KEY_Location, engineerInfo.location) // Change to the appropriate type
        values.put(AllResponseModel.KEY_Experties, engineerInfo.experties)
        values.put(AllResponseModel.KEY_ProfilePic, engineerInfo.profilePic)
        values.put(AllResponseModel.KEY_IsNotification, engineerInfo.isNotification)
        values.put(AllResponseModel.KEY_FcmId, engineerInfo.fcmId)
        values.put(AllResponseModel.KEY_DeviceType, engineerInfo.deviceType)
        values.put(AllResponseModel.KEY_IsActive, engineerInfo.isActive)
        values.put(AllResponseModel.KEY_Role, engineerInfo.role)
        values.put(AllResponseModel.KEY_Assigned, engineerInfo.assigned)
        values.put(AllResponseModel.KEY_Closed, engineerInfo.closed)
        values.put(AllResponseModel.KEY_InProgress, engineerInfo.inProgress)
        values.put(AllResponseModel.KEY_Token, engineerInfo.token)

        if (!isExist(engineerInfo)) {
            E("insert successfully")
            E("Values::$values")
            db!!.insert(AllResponseModel.TABLE_NAME, null, values)
        } else {

            E("update successfully")
            db!!.update(
                AllResponseModel.TABLE_NAME,
                values,
                AllResponseModel.KEY_ID + "='" + engineerInfo.userId + "'",
                null
            )
        }
        close()
    }
    //    All Key
//    const val KEY__ID = "_id"
//    const val KEY_ID = "userId"
//    const val KEY_Uuid = "uuid"
//    const val KEY_RoleId = "roleId"
//    const val KEY_UserName = "userName"
//    const val KEY_LastName = "lastName"
//    const val KEY_Email = "email"
//    const val KEY_Mobile = "mobile"
//    const val KEY_Location = "location"
//    const val KEY_Experties = "experties"
//    const val KEY_ProfilePic = "profilePic"
//    const val KEY_IsNotification = "isNotification"
//    const val KEY_FcmId = "fcmId"
//    const val KEY_DeviceType = "deviceType"
//    const val KEY_IsActive = "isActive"
//    const val KEY_Role = "role"
//    const val KEY_Assigned = "assigned"
//    const val KEY_Closed = "closed"
//    const val KEY_InProgress = "inProgress"
//    const val KEY_Token = "token"

    fun updateAllData(list:EngineerProfile)
    {
        try {
            open()
            val id = 1
            val cv = ContentValues()
            cv.put(AllResponseModel.KEY_ID, list.userId)
            cv.put(AllResponseModel.KEY_Uuid, list.uuid)
            cv.put(AllResponseModel.KEY_RoleId, list.roleId)
            cv.put(AllResponseModel.KEY_UserName, list.userName)
            cv.put(AllResponseModel.KEY_LastName, list.lastName)
            cv.put(AllResponseModel.KEY_Email, list.email)
            cv.put(AllResponseModel.KEY_Mobile, list.mobile)
            cv.put(AllResponseModel.KEY_Location, list.location)
            cv.put(AllResponseModel.KEY_Experties, list.experties)
            cv.put(AllResponseModel.KEY_ProfilePic, list.profilePic)
            cv.put(AllResponseModel.KEY_IsNotification, list.isNotification)
            cv.put(AllResponseModel.KEY_FcmId, list.fcmId)
            cv.put(AllResponseModel.KEY_DeviceType, list.deviceType)
            cv.put(AllResponseModel.KEY_IsActive, list.isActive)
            cv.put(AllResponseModel.KEY_Role, list.role)
            cv.put(AllResponseModel.KEY_Assigned, list.assigned)
            cv.put(AllResponseModel.KEY_Closed, list.closed)
            cv.put(AllResponseModel.KEY_InProgress, list.inProgress)
            cv.put(AllResponseModel.KEY_InProgress, list.inProgress)

            val rowsAffected = db?.update(AllResponseModel.TABLE_NAME, cv, "${AllResponseModel.KEY__ID} = ?", arrayOf("$id"))

        } catch (e: Exception) {
            Log.e("UpdateNotification", "Error updating notification: ${e.message}")
        } finally {
            db?.close()
        }
    }

    fun updateNotification(is_notification: String)
    {
        try {
            open()
            val id = 1
            val cv = ContentValues()
            cv.put(AllResponseModel.KEY_IsNotification, is_notification)

            val rowsAffected = db?.update(AllResponseModel.TABLE_NAME, cv, "${AllResponseModel.KEY__ID} = ?", arrayOf("$id"))

        } catch (e: Exception) {
            Log.e("UpdateNotification", "Error updating notification: ${e.message}")
        } finally {
            db?.close()
        }
    }

    /**
     * Return EngineerInfo ArrayList
     *
     * @return //
     */
    @get:SuppressLint("Range")
    val list: ArrayList<EngineerInfo>
        get() {

            val engineerInfoList = ArrayList<EngineerInfo>()
            read()
            val cursor = db!!.rawQuery("SELECT * FROM " + AllResponseModel.TABLE_NAME, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToLast()
                do {
                    val engineerInfo = EngineerInfo()
                    engineerInfo.userId = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_ID))
                    engineerInfo.uuid = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Uuid))
                    engineerInfo.roleId = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_RoleId))
                    engineerInfo.userName = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_UserName))
                    engineerInfo.lastName = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_LastName))
                    engineerInfo.email = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Email))
                    engineerInfo.mobile = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Mobile))
                    engineerInfo.location = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Location)) // Change to the appropriate type
                    engineerInfo.experties = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Experties))
                    engineerInfo.profilePic = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_ProfilePic))
                    engineerInfo.isNotification = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_IsNotification))
                    engineerInfo.fcmId = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_FcmId))
                    engineerInfo.deviceType = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_DeviceType))
                    engineerInfo.isActive = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_IsActive))
                    engineerInfo.role = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Role))
                    engineerInfo.assigned = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Assigned))
                    engineerInfo.closed = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Closed))
                    engineerInfo.inProgress = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_InProgress))
                    engineerInfo.token = cursor.getString(cursor.getColumnIndex(AllResponseModel.KEY_Token))
                    engineerInfoList.add(engineerInfo)

                } while (cursor.moveToPrevious())
                cursor.close()
            }
            close()
            return engineerInfoList
        }

    companion object {
        /**
         * EngineerInfoHelper instance
         *
         * @return //
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: UserDataHelper
            private set
    }
}