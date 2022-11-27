package com.fastturtle.RememberAnyOne.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

import com.fastturtle.RememberAnyOne.helperClasses.DatabaseHelper;
import com.fastturtle.RememberAnyOne.NotificationHelper;
import com.fastturtle.RememberAnyOne.R;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.regex.Pattern;

public class AddUserActivity extends AppCompatActivity implements OnClickListener {

    int year, month, day;
    AppCompatButton btnAdd, btnCancel, btnCamera;
    AppCompatImageView capturedImage, imgCalendarIcon;
    AppCompatEditText etName, etAge, etEmail, etMobile;
    AppCompatTextView tvDOB;
    private static final int CAMERA_REQUEST = 1888;
    DatabaseHelper myDb;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    Drawable oldDrawable;
    Bitmap bitmap;
    NotificationHelper notificationHelper;
    public static final int SELECT_PICTURE = 100;
    FloatingActionButton fabImageSelector;
    public static final int DATE_PICKER_ID = 0;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AddUserActivity.this, DashBoardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_user);

        myDb = new DatabaseHelper(this);
        tvDOB = findViewById(R.id.textViewDOB);
        etName = findViewById(R.id.editTextName);
        etAge = findViewById(R.id.editTextAge);
        etEmail = findViewById(R.id.editTextEmail);
        etMobile = findViewById(R.id.editTextMobile);
        btnCamera = findViewById(R.id.buttonTakePhoto);
        btnAdd = findViewById(R.id.buttonAddOrUpdate);
        btnCancel = findViewById(R.id.buttonCancelIns);
        fabImageSelector = findViewById(R.id.fabImageSelector);
        imgCalendarIcon = findViewById(R.id.imgDobSelector);
        capturedImage = findViewById(R.id.capturedImage);
        oldDrawable = capturedImage.getDrawable();
        notificationHelper = new NotificationHelper(this);
        btnAdd.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgCalendarIcon.setOnClickListener(this);
        fabImageSelector.setOnClickListener(this);

        btnAdd.setText(getString(R.string.add));

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddOrUpdate) {
            sName = etName.getText().toString();
            sAge = etAge.getText().toString();
            sEmail = etEmail.getText().toString();
            sMobile = etMobile.getText().toString();
            sDOB = tvDOB.getText().toString();
            if (!sAge.isEmpty())
                valAge = Integer.parseInt(sAge);

            if (sName.isEmpty() || sAge.isEmpty() || sEmail.isEmpty() || sMobile.isEmpty() || sDOB.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Field is left blank", Toast.LENGTH_SHORT).show();
            } else if (!validEmail(sEmail)) {
                etEmail.setError("Enter valid Email");
            } else if (sMobile.length() != 10) {
                etMobile.setError("Enter valid Mobile No.");
            } else if (valAge < 15 || valAge > 120) {
                etAge.setError("Enter age between 15 & 120");
            } else if (myDb.CheckDuplicateUser(sEmail)) {
                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
            } else if (capturedImage.getDrawable() == oldDrawable) {
                Toast.makeText(getApplicationContext(), "Please click on " + "\"Take Photo\"" + " Button to click photo", Toast.LENGTH_LONG).show();
            } else {
                myDb.insertData(sName, sAge, sEmail, sMobile, sDOB, getBytes(bitmap));

                notificationHelper.createNotification("New user added to database", sName + " with email " + sEmail + " added");
                capturedImage.setImageResource(R.drawable.icon_capture);
                etName.setText("");
                etAge.setText("");
                etEmail.setText("");
                etMobile.setText("");
                tvDOB.setText("");
            }

        } else if (v.getId() == R.id.buttonCancelIns) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.buttonTakePhoto) {
            // //camera stuff
            PackageManager pm = getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getBaseContext(), "Camera not available", Toast.LENGTH_LONG).show();
            }

        } else if (v.getId() == R.id.fabImageSelector) {
            openImageChooser();
        } else if (v.getId() == R.id.imgDobSelector) {
            select_date();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            capturedImage.setImageBitmap(bitmap);

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
        } else if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                String path = getPathFromUri(selectedImageUri);
                Log.i("AddUserActivity", "Image Path : " + path);
                capturedImage.setImageURI(selectedImageUri);
                BitmapDrawable drawable = (BitmapDrawable) capturedImage.getDrawable();
                bitmap = drawable.getBitmap();
            }
        }
    }

    public boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public String getPathFromUri(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void select_date() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        showDialog(DATE_PICKER_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, pickerListener, year, month, day);
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDayOfMonth;
            tvDOB.setText(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year));
        }
    };
}
