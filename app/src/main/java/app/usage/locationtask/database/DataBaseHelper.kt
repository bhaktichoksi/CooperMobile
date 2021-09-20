package app.usage.locationtask.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.util.*

class DataBaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_FNAME + " TEXT," + COLUMN_USER_LNAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PINCODE + " TEXT" + ")")

    // drop table sql query
    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_USER_TABLE)

        onCreate(db)
    }

    //Fetching All User

    fun getAllUser(): List<UserModel> {

        // array of columns to fetch
        val columns = arrayOf(
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_FNAME,
                COLUMN_USER_LNAME,
                COLUMN_USER_PINCODE


        )

        // sorting orders
        val sortOrder = "$COLUMN_USER_ID ASC"
        val userList = ArrayList<UserModel>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
                TABLE_USER, //Table to query
                columns,            //columns to return
                null,     //columns for the WHERE clause
                null,  //The values for the WHERE clause
                null,      //group the rows
                null,       //filter by row groups
                sortOrder
        )         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val user = UserModel(
                        id = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)).toInt(),
                        name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FNAME)),
                        lat = cursor.getString(cursor.getColumnIndex(COLUMN_USER_LNAME)),
                        Long = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                        pinCode = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PINCODE))

                        )

                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    //Add User
    fun addUser(user: UserModel) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_FNAME, user.name)
        values.put(COLUMN_USER_LNAME, user.lat)
        values.put(COLUMN_USER_EMAIL, user.Long)
        values.put(COLUMN_USER_PINCODE, user.pinCode)

        // Inserting Row
        db.insert(TABLE_USER, null, values)
        db.close()
    }

    //Update user
    fun updateUser(user: UserModel) {

        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_FNAME, user.name)
        values.put(COLUMN_USER_LNAME, user.lat)
        values.put(COLUMN_USER_EMAIL, user.Long)


        // updating row
        db.update(
                TABLE_USER, values, "$COLUMN_USER_ID = ?",
                arrayOf(user.id.toString())
        )
        db.close()
    }

    //Delete User
    fun deleteUser(id: String) {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(
                TABLE_USER, "$COLUMN_USER_ID = ?",
                arrayOf(id)
        )
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '$TABLE_USER'")
        db.close()
    }


    //Check user if exist or Not through Email
    fun checkUser(email: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase

        // selection criteria
        val selection = "$COLUMN_USER_FNAME = ?"

        // selection argument
        val selectionArgs = arrayOf(email)

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        val cursor = db.query(
                TABLE_USER, //Table to query
                columns,        //columns to return
                selection,      //columns for the WHERE clause
                selectionArgs,  //The values for the WHERE clause
                null,  //group the rows
                null,   //filter by row groups
                null
        )  //The sort order


        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */


    companion object {
        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "UserManager.db"

        // User table name
        private val TABLE_USER = "place"

        // User Table Columns names
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_FNAME = "name"
        private val COLUMN_USER_LNAME = "lat"
        private val COLUMN_USER_EMAIL = "long"
        private val COLUMN_USER_PINCODE = "pincode"
    }
}
