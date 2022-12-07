package com.fastturtle.rememberMe.activities;

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
import android.text.TextUtils;
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

import com.fastturtle.rememberMe.R;
import com.fastturtle.rememberMe.fragments.DatePickerFragment;
import com.fastturtle.rememberMe.helperClasses.BitmapCompressionTask;
import com.fastturtle.rememberMe.helperClasses.Constants;
import com.fastturtle.rememberMe.helperClasses.DatabaseHelper;
import com.fastturtle.rememberMe.helperClasses.Utils;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener
        , BitmapCompressionTask.BitmapCompressedListener
        , ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    AppCompatButton btnUpdate, btnCancel, btnCamera, btnSelectPhoto;
    AppCompatEditText etName, etEmail, etMobile;
    DatabaseHelper myDb;
    AppCompatTextView tvDOB, tvAge;
    AppCompatImageView capturedImage, imgDOBSelector;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    Bitmap bitmap, bitmapFromImageView;
    int userId;

    ActivityResultLauncher<Intent> takePhotoForResult, pickImageFromGalleryForResult;
    BitmapCompressionTask bitmapCompressionTask;
//    Disposable disposable;


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
        btnSelectPhoto = findViewById(R.id.buttonChooseFromGallery);
        imgDOBSelector = findViewById(R.id.imgDobSelector);
        btnCamera.setText(getString(R.string.take_new_photo));
        btnUpdate.setText(getString(R.string.update));

        btnCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnSelectPhoto.setOnClickListener(this);
        imgDOBSelector.setOnClickListener(this);

        userId = i.getExtras().getInt("UserId");
        byte[] byteArrayBmp = i.getByteArrayExtra("BITMAP_SHARED_KEY");
        bitmap = Utils.getImage(byteArrayBmp);
        capturedImage = findViewById(R.id.capturedImage);
        capturedImage.setImageBitmap(bitmap);
        Cursor c = myDb.getAllDataUsingId(userId);

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
                                bitmapCompressionTask = new BitmapCompressionTask(UpdateUserActivity.this,UpdateUserActivity.this);
                                bitmapCompressionTask.execute(selectedImageUri);
//                                disposable = Single.fromCallable(() -> {
//                                            bitmap = MediaStore.Images.Media.getBitmap(UpdateUserActivity.this.getContentResolver(), selectedImageUri);
//                                            bitmap = Utils.getCompressedBitmap(bitmap, 300);
//                                            return bitmap;
//                                        })
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe((Consumer<Object>) o -> {
//                                            capturedImage.setImageBitmap((Bitmap) o);
//                                            BitmapDrawable drawable = (BitmapDrawable) capturedImage.getDrawable();
//                                            this.bitmap = drawable.getBitmap();
//                                        });
                            }
                        }
                    }
                });


    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (disposable != null)
//            disposable.dispose();
//    }

    @Override
    public void onBitmapCompressed(Bitmap bitmap) {
        capturedImage.setImageBitmap(bitmap);
        BitmapDrawable drawable = (BitmapDrawable) capturedImage.getDrawable();
        this.bitmap = drawable.getBitmap();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        bitmapFromImageView = ((BitmapDrawable) capturedImage.getDrawable()).getBitmap();
        if (bitmapFromImageView != null)
            outState.putByteArray("bitmap", Utils.getBytes(bitmapFromImageView));
        if (!TextUtils.isEmpty(etName.getText()))
            outState.putString("name", etName.getText().toString());
        if (!tvDOB.getText().toString().isEmpty())
            outState.putString("dob", tvDOB.getText().toString());
        if (!tvAge.getText().toString().isEmpty() && !tvAge.getText().toString().equals("--"))
            outState.putString("age", tvAge.getText().toString());
        if (!TextUtils.isEmpty(etEmail.getText()))
            outState.putString("email", etEmail.getText().toString());
        if (!TextUtils.isEmpty(etMobile.getText()))
            outState.putString("mobno", etMobile.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("bitmap")) {
            bitmap = Utils.getImage(savedInstanceState.getByteArray("bitmap"));
            capturedImage.setImageBitmap(bitmap);
        }
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
    public void onClick(View v) {
        if (v.getId() == R.id.buttonTakePhoto) {
            // //camera stuff
            PackageManager pm = getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (Utils.notHavePermissions(this, Constants.CAMERA_PERMISSION)) {
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
            if (!sAge.isEmpty() && !sAge.equals("--"))
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
                myDb.updateUser(userId, sName, sAge, sEmail, sMobile, sDOB, Utils.getBytes(bitmap));
                startActivity(new Intent(this, AllUsersListActivity.class));
                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                finish();

            }

        } else if (v.getId() == R.id.buttonCancelIns) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.imgDobSelector) {
            selectDate();
        } else if (v.getId() == R.id.buttonChooseFromGallery) {
            openImageChooser();
        }
    }

    void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.setType("image/jpeg|image/png");
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
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
                takePhotoForResult.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Camera permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
