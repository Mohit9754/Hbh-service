package com.dollop.hbh_service_engineer.basic.Database

import android.content.*
import android.database.sqlite.SQLiteQueryBuilder
import android.database.sqlite.SQLiteException
import android.os.Build
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.net.Uri
import java.lang.IllegalArgumentException
import java.util.HashMap

class MyContentProvider : ContentProvider() {
    override fun getType(uri: Uri): String? {
        return when (uriMatcher!!.match(uri)) {
            uriCode -> "vnd.android.cursor.dir/users"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    // creating the database
    override fun onCreate(): Boolean {
        instance = context
        val dbHelper = DatabaseHelper(instance)
        db = dbHelper.writableDatabase
        return if (db != null) {
            true
        } else false
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        qb.isStrict = true
        when (uriMatcher!!.match(uri)) {
            uriCode -> qb.projectionMap = values
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        if (sortOrder == null || sortOrder === "") {
            sortOrder = id
        }
        val c = qb.query(
            db, projection, selection, selectionArgs, null,
            null, sortOrder
        )
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    // adding data to the database
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowID = db!!.insert(TABLE_NAME, "", values)
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLiteException("Failed to add a record into $uri")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var count = 0
        when (uriMatcher!!.match(uri)) {
            uriCode -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val builder = SQLiteQueryBuilder()
                builder.tables = TABLE_NAME
                builder.projectionMap = Companion.values
                builder.isStrict = true
                count = builder.update(db!!, values!!, selection, selectionArgs)
            } else {
                count = db!!.update(TABLE_NAME, values, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        when (uriMatcher!!.match(uri)) {
            uriCode ->                // count = db.delete(TABLE_NAME, selection, selectionArgs);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val builder = SQLiteQueryBuilder()
                    builder.tables = TABLE_NAME
                    builder.projectionMap = values
                    builder.isStrict = true
                    count = builder.delete(db!!, selection, selectionArgs)
                } else {
                    count = db!!.delete(TABLE_NAME, selection, selectionArgs)
                }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    // creating object of database
    // to perform query
    private var db: SQLiteDatabase? = null

    // creating a database
    private class DatabaseHelper  // defining a constructor
    internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        // creating a table in the database
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
            onCreate(db)
        }
    }

    companion object {
        // defining authority so that other application can access it
        const val PROVIDER_NAME = "com.examoneOone.Credential"

        // defining content URI
        const val URL = "content://" + PROVIDER_NAME + "/users"

        // parsing the content URI
        val CONTENT_URI = Uri.parse(URL)
        const val id = "id"
        const val token = "token"
        const val uriCode = 1
        var uriMatcher: UriMatcher? = null
        private val values: HashMap<String, String>? = null
        private var instance: Context? = null

        init {

            // to match the content URI
            // every time user access table under content provider
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

            // to access whole table
            uriMatcher!!.addURI(PROVIDER_NAME, "users", uriCode)

            // to access a particular row
            // of the table
            uriMatcher!!.addURI(PROVIDER_NAME, "users/*", uriCode)
        }

        fun deleteAll() {
            val dbHelper = DatabaseHelper(instance)
            dbHelper.writableDatabase.delete(TABLE_NAME, null, null)
            dbHelper.close()
        }

        // declaring name of the database
        const val DATABASE_NAME = "Credential"

        // declaring table name of the database
        const val TABLE_NAME = "Users"

        // declaring version of the database
        const val DATABASE_VERSION = 1

        // sql query to create the table
        const val CREATE_DB_TABLE = (" CREATE TABLE " + TABLE_NAME
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " token TEXT NOT NULL);")
    }
}