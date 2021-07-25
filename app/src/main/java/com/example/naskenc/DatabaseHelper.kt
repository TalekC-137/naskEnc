package com.example.naskenc

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteException
import com.example.naskenc.models.CryptedModel
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "CryptedDatabases.db"

        private val TABLE_CRYPTED = "CryptedTables"

        private val KEY_ID = "_id"
        private val KEY_TITLE = "title"
        private val KEY_NOTE = "note"
      //  private val PASS_AES = "!@FSPA" //password for encryption/decryption
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CRYPTED + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " STRING, "
                + KEY_NOTE + " STRING "
                + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CRYPTED")
        onCreate(db)
    }

    fun addRow(crypted: CryptedModel, PASS_AES: String): Long{

        val db = this.getWritableDatabase(PASS_AES)

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, crypted.title)
        contentValues.put(KEY_NOTE, crypted.note)

        val success = db.insert(TABLE_CRYPTED, null, contentValues)

        db.close() // Closing database connection
        return success
    }


    fun viewEmployee(PASS_AES: String): ArrayList<CryptedModel> {

        val empList: ArrayList<CryptedModel> = ArrayList<CryptedModel>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_CRYPTED"

        val db = this.getReadableDatabase(PASS_AES)
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var title: String
        var note: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                note = cursor.getString(cursor.getColumnIndex(KEY_NOTE))

                val emp = CryptedModel(id = id, title = title, note = note)
                empList.add(emp)

            } while (cursor.moveToNext())
        }
        return empList
    }


}