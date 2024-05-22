package com.dollop.hbh_service_engineer.basic.Database

import android.content.ContentValues
import android.content.Context
import com.dollop.hbh_service_engineer.main_package.model.AllResponseModel.Companion.CreateTable
import com.dollop.hbh_service_engineer.main_package.model.AllResponseModel.Companion.dropTable
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

/**
 * Created by Anil on 9/4/2021.
 */
class DataManager
/**
 * @param context //
 * @param name    //
 * @param factory //
 * @param version //
 */
    (context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    /**
     * @param db //
     */
    override fun onCreate(db: SQLiteDatabase) {
        CreateTable(db)
    }

    /**
     * @param db        //
     * @param paramInt1 //
     * @param paramInt2 //
     */
    override fun onUpgrade(db: SQLiteDatabase, paramInt1: Int, paramInt2: Int) {
        dropTable(db)
        onCreate(db)
    }



    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "examoneOone"
    }
}