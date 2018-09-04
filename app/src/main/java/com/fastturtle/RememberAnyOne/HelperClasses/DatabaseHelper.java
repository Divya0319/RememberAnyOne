package com.fastturtle.RememberAnyOne.HelperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String Database_Name = "UserdetailsNew.db";

    private static final String Table_Name = "userdetails";
    public final String Id = "Id";
    public final String Name = "Name";
    public final String Age = "Age";
    public final String Email = "EMail";
    public final String Mobile_No = "MobNo";
    public final String DOB = "Dob";
    public final String Key_Image = "key_image";

    public DatabaseHelper(Context context) {
        super(context, Database_Name, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Table_Name + " (" + Id + " INTEGER PRIMARY KEY AUTOINCREMENT," + Name + " TEXT,"
                + Age + " INTEGER," + Email + " TEXT," + Mobile_No + " INTEGER," + DOB + " TEXT," + Key_Image + " BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    public void insertData(String name, String age, String email, String mobno, String dob, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Name, name);
        cv.put(Age, age);
        cv.put(Email, email);
        cv.put(Mobile_No, mobno);
        cv.put(DOB, dob);
        cv.put(Key_Image, image);
        db.insert(Table_Name, null, cv);
        db.close();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + Table_Name, null);
        return res;
    }

    public Cursor getAllDataUsingId(int Uid) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM " + Table_Name + " WHERE " + Id + " = " + Uid + " ", null);
        return c;

    }

    public void delete(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table_Name + " WHERE " + Id + " = " + userId + ";");
        db.close();
    }

    public void updateUser(int id, String name, String age, String email, String mobno, String dob, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String strFilter = Id + "=" + id;
        cv.put(Name, name);
        cv.put(Age, age);
        cv.put(Email, email);
        cv.put(Mobile_No, mobno);
        cv.put(DOB, dob);
        cv.put(Key_Image, image);
        db.update(Table_Name, cv, strFilter, null);
    }

    public boolean CheckDuplicateUser(String newEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Boolean response = false;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table_Name + " WHERE " + Email + " = ?",
                new String[]{newEmail});
        if (cursor != null && cursor.moveToFirst()) {
            String responseName = cursor.getString(cursor.getColumnIndex(Email));
            if (!responseName.isEmpty()) {
                response = true;
            }
        }
        cursor.close();
        return response;
    }

}
