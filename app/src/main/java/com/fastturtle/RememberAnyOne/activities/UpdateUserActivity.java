package com.fastturtle.RememberAnyOne.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.content.ContextCompat;

import com.fastturtle.RememberAnyOne.R;
import com.fastturtle.RememberAnyOne.fragments.DatePickerFragment;
import com.fastturtle.RememberAnyOne.helperClasses.Constants;
import com.fastturtle.RememberAnyOne.helperClasses.DatabaseHelper;
import com.fastturtle.RememberAnyOne.helperClasses.Utils;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    AppCompatButton btnUpdate, btnCancel, btnCamera, btSelectImage;
    AppCompatEditText etName, etEmail, etMobile;
    private static final int CAMERA_REQUEST = 1888;
    DatabaseHelper myDb;
    AppCompatTextView tvDOB, tvAge;
    AppCompatImageView capturedImage, imgDOBSelector;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    Bitmap bitmap;
    int UserId;

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

        UserId = i.getExtras().getInt("UserId");
        byte[] byteArrayBmp = i.getByteArrayExtra("BITMAP_SHARED_KEY");
        bitmap = Utils.getImage(byteArrayBmp);
        capturedImage = findViewById(R.id.capturedImage);
        capturedImage.setImageBitmap(bitmap);
        Cursor c = myDb.getAllDataUsingId(UserId);

        if (c.moveToFirst()) {
            do {
                etName.setText(c.getString(c.getColumnIndexOrThrow(Constants.Name)));
                tvAge.setText(c.getString(c.getColumnIndexOrThrow(Constants.Age)));
                etEmail.setText(c.getString(c.getColumnIndexOrThrow(Constants.Email)));
                etMobile.setText(c.getString(c.getColumnIndexOrThrow(Constants.Mobile_No)));
                tvDOB.setText(c.getString(c.getColumnIndexOrThrow(Constants.DOB)));

            } while (c.moveToNext());
        }
        c.close();

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
                                String path = Utils.getPathFromUri(this, selectedImageUri);
                                Log.d("Image path", path);
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
        if (v.getId() == R.id.buttonTakePhoto) {
            // //camera stuff
            PackageManager pm = getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (ContextCompat.checkSelfPermission(UpdateUserActivity.this, Constants.CAMERA_PERMISSION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateUserActivity.this,
                            new String[]{Constants.CAMERA_PERMISSION}, Constants.RC_CAMERA);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhotoForResult.launch(cameraIntent);
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
                myDb.updateUser(UserId, sName, sAge, sEmail, sMobile, sDOB, Utils.getBytes(bitmap));
                capturedImage.setImageResource(R.drawable.icon_capture);
                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_LONG).show();
                etName.setText("");
                tvAge.setText("--");
                etEmail.setText("");
                etMobile.setText("");

            }

        } else if (v.getId() == R.id.buttonCancelIns) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.btImageSelector) {
                openImageChooser();
        } else if (v.getId() == R.id.imgDobSelector) {
            selectDate();
        }
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            capturedImage.setImageBitmap(bitmap);

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
        } else if (requestCode == Constants.SELECT_PICTURE) {
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

    void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        pickImageFromGalleryForResult.launch(galleryIntent);
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
