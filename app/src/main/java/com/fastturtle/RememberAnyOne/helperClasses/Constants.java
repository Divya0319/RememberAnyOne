package com.fastturtle.RememberAnyOne.helperClasses;

import android.Manifest;

public interface Constants {
    int SELECT_PICTURE = 100;

    // Notification constants
    String NOTIFICATION_CHANNEL_ID = "user_added_channel";
    String NOTIFICATION_CHANNEL_NAME = "User created";
    int NOTIFY_ID = 1;

    // Permission constants
    String CAMERA_PERMISSIONS = Manifest.permission.CAMERA;
    String[] CAMERA_STORAGE_PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int RC_CAMERA_STORAGE = 123;
    int RC_CAMERA = 456;
    int RC_STORAGE = 789;

    // Database constants
    String Database_Name = "UserdetailsNew.db";
    String Table_Name = "userdetails";
    String Id = "Id";
    String Name = "Name";
    String Age = "Age";
    String Email = "EMail";
    String Mobile_No = "MobNo";
    String DOB = "Dob";
    String Key_Image = "key_image";

}
