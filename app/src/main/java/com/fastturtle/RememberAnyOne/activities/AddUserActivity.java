package com.fastturtle.RememberAnyOne.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.fastturtle.RememberAnyOne.NotificationHelper;
import com.fastturtle.RememberAnyOne.R;
import com.fastturtle.RememberAnyOne.fragments.DatePickerFragment;
import com.fastturtle.RememberAnyOne.helperClasses.Constants;
import com.fastturtle.RememberAnyOne.helperClasses.DatabaseHelper;
import com.fastturtle.RememberAnyOne.helperClasses.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddUserActivity extends AppCompatActivity implements OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    AppCompatButton btnAdd, btnCancel, btnCamera, btSelectImage;
    AppCompatImageView capturedImage, imgCalendarIcon;
    AppCompatEditText etName, etEmail, etMobile;
    AppCompatTextView tvDOB, tvAge;
    DatabaseHelper myDb;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    Drawable oldDrawable;
    NotificationHelper notificationHelper;

    ActivityResultLauncher<Intent> takePhotoForResult, pickImageFromGalleryForResult;
    String imageFilePath;
    Uri capturedImageUri;

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
        tvAge = findViewById(R.id.tv_age);
        etEmail = findViewById(R.id.editTextEmail);
        etMobile = findViewById(R.id.editTextMobile);
        btnCamera = findViewById(R.id.buttonTakePhoto);
        btnAdd = findViewById(R.id.buttonAddOrUpdate);
        btnCancel = findViewById(R.id.buttonCancelIns);
        btSelectImage = findViewById(R.id.btImageSelector);
        imgCalendarIcon = findViewById(R.id.imgDobSelector);
        capturedImage = findViewById(R.id.capturedImage);
        oldDrawable = capturedImage.getDrawable();
        notificationHelper = new NotificationHelper(this);
        btnAdd.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgCalendarIcon.setOnClickListener(this);
        btSelectImage.setOnClickListener(this);

        btnAdd.setText(getString(R.string.add));

        takePhotoForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ContentResolver cr = getApplicationContext().getContentResolver();
                        InputStream is = null;
                        try {
                            is = cr.openInputStream(capturedImageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        capturedImage.setImageBitmap(bitmap);
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageFromGalleryForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            capturedImageUri = result.getData().getData();
                            if (capturedImageUri != null) {
                                String path = Utils.getPathFromUri(this, capturedImageUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 8;
                                Bitmap bitmap = BitmapFactory.decodeFile(path);
                                capturedImage.setImageBitmap(bitmap);
                            }
                        }
                    }
                });

    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());

        File storageFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timeStamp,
                ".jpg",
                storageFile);
        imageFilePath = image.getAbsolutePath();
        return image;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddOrUpdate) {
            sName = etName.getText().toString();
            sAge = tvAge.getText().toString();
            sEmail = etEmail.getText().toString();
            sMobile = etMobile.getText().toString();
            sDOB = tvDOB.getText().toString();
            if (!sAge.isEmpty())
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
                tvAge.setError(null);
                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
            } else if (capturedImage.getDrawable() == oldDrawable) {
                tvAge.setError(null);
                Toast.makeText(getApplicationContext(), "Please click on " + "\"Take Photo\"" + " Button to click photo", Toast.LENGTH_LONG).show();
            } else {
                myDb.insertData(sName, sAge, sEmail, sMobile, sDOB, capturedImageUri.toString());

                notificationHelper.createNotification("New user added to database", sName + " with email " + sEmail + " added");
                capturedImage.setImageResource(R.drawable.icon_capture);
                etName.setText("");
                tvAge.setText("--");
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
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (Utils.notHavePermissions(this, Constants.CAMERA_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(AddUserActivity.this,
                            new String[]{Constants.CAMERA_PERMISSIONS}, Constants.RC_CAMERA);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(this,
                                "com.fastturtle.RememberAnyOne.fileprovider", photoFile);
                        capturedImageUri = photoUri;

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        takePhotoForResult.launch(cameraIntent);
                    }
                }

            } else {
                Toast.makeText(getBaseContext(), "Camera not available", Toast.LENGTH_LONG).show();
            }

        } else if (v.getId() == R.id.btImageSelector) {
            if (Utils.notHavePermissions(this, Constants.STORAGE_PERMISSIONS)) {
                ActivityCompat.requestPermissions(AddUserActivity.this,
                        Constants.STORAGE_PERMISSIONS, Constants.RC_STORAGE);
            } else {
                openImageChooser();
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
        tvAge.setText(String.valueOf(Utils.findAge(year, monthOfYear, dayOfMonth)));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.RC_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this,
                            "com.fastturtle.RememberAnyOne.fileprovider", photoFile);
                    capturedImageUri = photoUri;

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    takePhotoForResult.launch(cameraIntent);
                }
            } else {
                Toast.makeText(this, "Camera permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RC_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Storage permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
