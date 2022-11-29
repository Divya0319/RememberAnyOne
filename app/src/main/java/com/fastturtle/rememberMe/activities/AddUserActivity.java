package com.fastturtle.rememberMe.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fastturtle.rememberMe.NotificationHelper;
import com.fastturtle.rememberMe.fragments.DatePickerFragment;
import com.fastturtle.rememberMe.helperClasses.Constants;
import com.fastturtle.rememberMe.helperClasses.DatabaseHelper;
import com.fastturtle.rememberMe.helperClasses.Utils;
import com.fastturtle.rememberme.R;

public class AddUserActivity extends AppCompatActivity implements OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    AppCompatButton btnAdd, btnCancel, btnCamera;
    AppCompatImageView capturedImage, imgCalendarIcon;
    AppCompatEditText etName, etEmail, etMobile;
    AppCompatTextView tvDOB, tvAge;
    DatabaseHelper myDb;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    Drawable oldDrawable;
    Bitmap bitmap, bitmapFromImageView;
    NotificationHelper notificationHelper;

    ActivityResultLauncher<Intent> takePhotoForResult, pickImageFromGalleryForResult;

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        bitmapFromImageView = ((BitmapDrawable)capturedImage.getDrawable()).getBitmap();
        if (bitmapFromImageView != null)
            outState.putByteArray("bitmap", Utils.getBytes(bitmapFromImageView));
        if (!TextUtils.isEmpty(etName.getText()))
            outState.putString("name", etName.getText().toString());
        if (!TextUtils.isEmpty(tvDOB.getText()))
            outState.putString("dob", tvDOB.getText().toString());
        if (!TextUtils.isEmpty(tvAge.getText()) && !tvAge.getText().toString().equals("--"))
            outState.putString("age", tvAge.getText().toString());
        if (!TextUtils.isEmpty(etEmail.getText()))
            outState.putString("email", etEmail.getText().toString());
        if (!TextUtils.isEmpty(etMobile.getText()))
            outState.putString("mobno", etMobile.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("bitmap"))
            bitmap = Utils.getImage(savedInstanceState.getByteArray("bitmap"));
            capturedImage.setImageBitmap(bitmap);
        if (savedInstanceState.containsKey("name"))
            etName.setText(savedInstanceState.getString("name"));
        if (savedInstanceState.containsKey("dob"))
            tvDOB.setText(savedInstanceState.getString("dob"));
        if (savedInstanceState.containsKey("age"))
            tvAge.setText(savedInstanceState.getString("age"));
        if (savedInstanceState.containsKey("email"))
            etEmail.setText(savedInstanceState.getString("email"));
        if (savedInstanceState.containsKey("mobno"))
            etMobile.setText(savedInstanceState.getString("mobno"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_user);

        myDb = new DatabaseHelper(this);
        tvDOB = findViewById(R.id.textViewDOB);
        etName = findViewById(R.id.editTextName);
        tvAge = findViewById(R.id.tv_age);
        etEmail = findViewById(R.id.editTextEmail);
        etMobile = findViewById(R.id.editTextMobile);
        btnCamera = findViewById(R.id.buttonTakePhoto);
        btnAdd = findViewById(R.id.buttonAddOrUpdate);
        btnCancel = findViewById(R.id.buttonCancelIns);
        imgCalendarIcon = findViewById(R.id.imgDobSelector);
        capturedImage = findViewById(R.id.capturedImage);
        oldDrawable = capturedImage.getDrawable();
        notificationHelper = new NotificationHelper(this);
        btnAdd.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgCalendarIcon.setOnClickListener(this);

        btnAdd.setText(getString(R.string.add));

        takePhotoForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            bitmap = (Bitmap) result.getData().getExtras().get("data");
                            capturedImage.setImageBitmap(bitmap);
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageFromGalleryForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                capturedImage.setImageURI(selectedImageUri);
                                BitmapDrawable drawable = (BitmapDrawable) capturedImage.getDrawable();
                                bitmap = drawable.getBitmap();
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddOrUpdate) {
            sName = etName.getText().toString();
            sAge = tvAge.getText().toString();
            sEmail = etEmail.getText().toString();
            sMobile = etMobile.getText().toString();
            sDOB = tvDOB.getText().toString();
            if (!sAge.isEmpty() && !sAge.equals("--"))
                valAge = Integer.parseInt(sAge);

            if (sName.isEmpty() || sAge.isEmpty() || sEmail.isEmpty() || sMobile.isEmpty() || sDOB.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Field is left blank", Toast.LENGTH_SHORT).show();
            } else if (Utils.invalidEmail(sEmail)) {
                etEmail.setError("Enter valid Email");
            } else if (sMobile.length() != 10) {
                etMobile.setError("Enter valid Mobile No.");
            } else if (valAge < 15 || valAge > 120) {
                tvAge.requestFocus();
                tvAge.setError("Age must be between 15 & 120");
            } else if (myDb.checkDuplicateUser(sEmail)) {
                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
            } else if (capturedImage.getDrawable() == oldDrawable) {
                Toast.makeText(getApplicationContext(), "Please click on " + "\"Take Photo\"" + " Button to click photo", Toast.LENGTH_LONG).show();
            } else {
                myDb.insertData(sName, sAge, sEmail, sMobile, sDOB, Utils.getBytes(bitmap));

                notificationHelper.createNotification("New user added to database", sName + " with email " + sEmail + " added");
                capturedImage.setImageResource(R.drawable.icon_capture);
                etName.setText("");
                tvAge.setText("--");
                etEmail.setText("");
                etMobile.setText("");
                tvDOB.setText("");
                bitmap = null;
                bitmapFromImageView = null;
            }

        } else if (v.getId() == R.id.buttonCancelIns) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.buttonTakePhoto) {
            // //camera stuff
            PackageManager pm = getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (ContextCompat.checkSelfPermission(AddUserActivity.this, Constants.CAMERA_PERMISSION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddUserActivity.this,
                            new String[]{Constants.CAMERA_PERMISSION}, Constants.RC_CAMERA);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhotoForResult.launch(cameraIntent);
                }

            } else {
                Toast.makeText(getBaseContext(), "Camera not available", Toast.LENGTH_LONG).show();
            }

        } else if (v.getId() == R.id.imgDobSelector) {
            selectDate();
        }
    }

    void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        pickImageFromGalleryForResult.launch(galleryIntent);
    }

    public void selectDate() {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        tvDOB.setText(new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear).append("-").append(year));
        tvAge.setText(Utils.findAge(year, monthOfYear, dayOfMonth));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.RC_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoForResult.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Camera permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
