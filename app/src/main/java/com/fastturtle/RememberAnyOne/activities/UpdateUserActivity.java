package com.fastturtle.RememberAnyOne.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
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

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    AppCompatButton btnUpdate, btnCancel, btnCamera, btSelectImage;
    AppCompatEditText etName, etEmail, etMobile;
    DatabaseHelper myDb;
    AppCompatTextView tvDOB, tvAge;
    AppCompatImageView capturedImage, imgDOBSelector;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    int userId;
    String imageUri;
    String imageFilePath;
    Uri capturedImageUri;

    ActivityResultLauncher<Intent> takePhotoForResult, pickImageFromGalleryForResult;


    @Override
    public void onBackPressed() {
        Intent i = new Intent(UpdateUserActivity.this, DashBoardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_user);

        myDb = new DatabaseHelper(this);
        capturedImage = findViewById(R.id.capturedImage);

        Intent i = getIntent();

        etName = findViewById(R.id.editTextName);
        tvAge = findViewById(R.id.tv_age);
        etEmail = findViewById(R.id.editTextEmail);
        etMobile = findViewById(R.id.editTextMobile);
        tvDOB = findViewById(R.id.textViewDOB);
        btnCamera = findViewById(R.id.buttonTakePhoto);
        btnUpdate = findViewById(R.id.buttonAddOrUpdate);
        btnCancel = findViewById(R.id.buttonCancelIns);
        btSelectImage = findViewById(R.id.btImageSelector);
        imgDOBSelector = findViewById(R.id.imgDobSelector);
        btnCamera.setText(getString(R.string.take_new_photo));
        btnUpdate.setText(getString(R.string.update));

        btnCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btSelectImage.setOnClickListener(this);
        imgDOBSelector.setOnClickListener(this);

        userId = i.getExtras().getInt("UserId");
        capturedImage = findViewById(R.id.capturedImage);
        Cursor c = myDb.getAllDataUsingId(userId);

        if (c.moveToFirst()) {
            do {
                etName.setText(c.getString(c.getColumnIndexOrThrow(Constants.Name)));
                tvAge.setText(c.getString(c.getColumnIndexOrThrow(Constants.Age)));
                etEmail.setText(c.getString(c.getColumnIndexOrThrow(Constants.Email)));
                etMobile.setText(c.getString(c.getColumnIndexOrThrow(Constants.Mobile_No)));
                tvDOB.setText(c.getString(c.getColumnIndexOrThrow(Constants.DOB)));
                imageUri = c.getString(c.getColumnIndexOrThrow(Constants.Key_Image));

            } while (c.moveToNext());
        }
        c.close();
        capturedImageUri = Uri.parse(imageUri);
        if(capturedImageUri.toString().contains("fileprovider")) {
            ContentResolver cr = getContentResolver();
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
        }
//        else {
//            if (capturedImageUri != null) {
//                String path = Utils.getPathFromUri(this, capturedImageUri);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 8;
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                capturedImage.setImageBitmap(bitmap);
//            }
//        }



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
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                String path = Utils.getPathFromUri(this, selectedImageUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 8;
                                Bitmap bitmap = BitmapFactory.decodeFile(path);
                                capturedImage.setImageBitmap(bitmap);
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonTakePhoto) {
            // //camera stuff
            PackageManager pm = getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (Utils.notHavePermissions(this, Constants.CAMERA_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(UpdateUserActivity.this,
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
        } else if (v.getId() == R.id.buttonAddOrUpdate) {
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
                tvAge.setError("Age must be between 15 & 120");
            } else {
                myDb.updateUser(userId, sName, sAge, sEmail, sMobile, sDOB, capturedImageUri.toString());
                capturedImage.setImageResource(R.drawable.icon_capture);
                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_LONG).show();
                etName.setText("");
                tvAge.setText("--");
                etEmail.setText("");
                etMobile.setText("");

                onBackPressed();

            }

        } else if (v.getId() == R.id.buttonCancelIns) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.btImageSelector) {
            if (Utils.notHavePermissions(this, Constants.CAMERA_PERMISSIONS)) {
                ActivityCompat.requestPermissions(UpdateUserActivity.this,
                        new String[]{Constants.CAMERA_PERMISSIONS}, Constants.RC_CAMERA);
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
