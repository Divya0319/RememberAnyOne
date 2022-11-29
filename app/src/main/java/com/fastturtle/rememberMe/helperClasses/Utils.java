package com.fastturtle.rememberMe.helperClasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Patterns;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Utils {

    public static boolean onCreateOfUpdateScreenCalledFirstTime = true;

    public static boolean invalidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return !pattern.matcher(email).matches();
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static String getPathFromUri(Context ctx, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ctx.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static String findAge(int DOBYear, int DOBMonth, int DOBDayOfMonth) {

        int age;
        final Calendar calendarToday = Calendar.getInstance();
        int currentYear = calendarToday.get(Calendar.YEAR);
        int currentMonth = calendarToday.get(Calendar.MONTH);
        int todayDay = calendarToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOBYear;

        if(DOBMonth > currentMonth) {
            --age;
        } else if(DOBMonth == currentMonth) {
            if(DOBDayOfMonth > todayDay) {
                --age;
            }
        }
        return String.valueOf(age);

    }
}
