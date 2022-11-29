package com.fastturtle.RememberAnyOne.helperClasses;

public interface Constants {
    int SELECT_PICTURE = 100;

    // Notification constants
     String NOTIFICATION_CHANNEL_ID = "user_added_channel";
     String NOTIFICATION_CHANNEL_NAME = "User created";
     int NOTIFY_ID = 1;

     // Permission constants
    String STORAGE_PERMISSIONS = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    String CAMERA_PERMISSION = android.Manifest.permission.CAMERA;
    int RC_CAMERA = 123;
    int RC_STORAGE = 456;

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
    boolean onCreateOfUpdateActivityCalledFirstTime = true;

}
