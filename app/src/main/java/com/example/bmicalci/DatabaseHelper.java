package com.example.bmicalci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bmi_app.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // BMI History table
    private static final String TABLE_BMI = "bmi_history";
    private static final String COL_BMI_ID = "id";
    private static final String COL_USER_FK = "user_id";
    private static final String COL_BMI = "bmi";
    private static final String COL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    public void clearBMIHistory(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BMI, COL_USER_FK + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_BMI + " (" +
                COL_BMI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_FK + " INTEGER, " +
                COL_BMI + " REAL, " +
                COL_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COL_USER_FK + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Register user
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Login check
    public int loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE username=? AND password=?",
                new String[]{username, password});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0); // return user_id
        }
        return -1; // invalid
    }

    // Save BMI
    public void saveBmi(int userId, double bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_FK, userId);
        values.put(COL_BMI, bmi);
        db.insert(TABLE_BMI, null, values);
    }

    // Get BMI history
    public Cursor getBmiHistory(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT bmi, date FROM " + TABLE_BMI +
                " WHERE user_id=? ORDER BY date DESC", new String[]{String.valueOf(userId)});
    }
}
