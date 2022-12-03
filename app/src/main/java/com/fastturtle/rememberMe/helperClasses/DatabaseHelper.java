package com.fastturtle.rememberMe.helperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, Constants.Database_Name, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Constants.Table_Name + " (" + Constants.Id + " INTEGER PRIMARY KEY AUTOINCREMENT," + Constants.Name + " TEXT,"
                + Constants.Age + " INTEGER," + Constants.Email + " TEXT," + Constants.Mobile_No + " INTEGER," + Constants.DOB + " TEXT," + Constants.Key_Image + " BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.Table_Name);
        onCreate(db);
    }

    public void insertData(String name, String age, String email, String mobno, String dob, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.Name, name);
        cv.put(Constants.Age, age);
        cv.put(Constants.Email, email);
        cv.put(Constants.Mobile_No, mobno);
        cv.put(Constants.DOB, dob);
        cv.put(Constants.Key_Image, image);
        db.insert(Constants.Table_Name, null, cv);
        db.close();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.Table_Name, null);
    }

    public Cursor getAllDataUsingId(int Uid) {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(" SELECT * FROM " + Constants.Table_Name + " WHERE " + Constants.Id + " = " + Uid + " ", null);

    }

    public void delete(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.Table_Name + " WHERE " + Constants.Id + " = " + userId + ";");
        db.close();
    }

    public void updateUser(int id, String name, String age, String email, String mobno, String dob, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String strFilter = Constants.Id + "=" + id;
        cv.put(Constants.Name, name);
        cv.put(Constants.Age, age);
        cv.put(Constants.Email, email);
        cv.put(Constants.Mobile_No, mobno);
        cv.put(Constants.DOB, dob);
        cv.put(Constants.Key_Image, image);
        db.update(Constants.Table_Name, cv, strFilter, null);
    }

    public boolean checkDuplicateUser(String newEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean response = false;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.Table_Name + " WHERE " + Constants.Email + " = ?",
                new String[]{newEmail});
        if (cursor != null && cursor.moveToFirst()) {
            String responseName = cursor.getString(cursor.getColumnIndexOrThrow(Constants.Email));
            if (!responseName.isEmpty()) {
                response = true;
            }
        }
        if(cursor != null)
            cursor.close();
        return response;
    }

}
