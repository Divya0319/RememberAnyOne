package com.fastturtle.rememberMe.helperClasses;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Patterns;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Utils {


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

    public static Bitmap getCompressedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String getPathFromUri(Context ctx, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ctx.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static int findAge(int DOBYear, int DOBMonth, int DOBDayOfMonth) {

        int age;
        final Calendar calendarToday = Calendar.getInstance();
        int currentYear = calendarToday.get(Calendar.YEAR);
        int currentMonth = calendarToday.get(Calendar.MONTH);
        int todayDay = calendarToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOBYear;

        if (DOBMonth > currentMonth) {
            --age;
        } else if (DOBMonth == currentMonth) {
            if (DOBDayOfMonth > todayDay) {
                --age;
            }
        }
        return age;

    }

    public static boolean notHavePermissions(Context ctx, String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }

        return false;
    }
}
