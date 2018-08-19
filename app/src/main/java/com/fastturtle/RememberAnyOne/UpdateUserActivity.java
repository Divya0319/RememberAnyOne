package com.fastturtle.RememberAnyOne;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.fastturtle.RememberAnyOne.AddUserActivity.SELECT_PICTURE;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener {

    int year, month, day;
    Button btnUpdate, btnCancel, btnCamera;
    EditText etName, etAge, etEmail, etMobile;
    private static final int CAMERA_REQUEST = 1888;
    DatabaseHelper myDb;
    TextView tvDOB;
    ImageView capturedImage, imgDOBSelector;
    String sName, sAge, sEmail, sMobile, sDOB;
    int valAge;
    AddUserActivity uD;
    Bitmap bitmap;
    int UserId;
    FloatingActionButton fabSelectImage;

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
        uD = new AddUserActivity();
        capturedImage = findViewById(R.id.capturedImage);

        Intent i = getIntent();

        etName = findViewById(R.id.editTextName);
        etAge = findViewById(R.id.editTextAge);
        etEmail = findViewById(R.id.editTextEmail);
        etMobile = findViewById(R.id.editTextMobile);
        tvDOB = findViewById(R.id.textViewDOB);
        btnCamera = findViewById(R.id.buttonTakePhoto);
        btnUpdate = findViewById(R.id.buttonAddOrUpdate);
        btnCancel = findViewById(R.id.buttonCancelIns);
        fabSelectImage = findViewById(R.id.fabImageSelector);
        imgDOBSelector = findViewById(R.id.imgDobSelector);
        btnCamera.setText(getString(R.string.take_new_photo));
        btnUpdate.setText(getString(R.string.update));

        btnCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        fabSelectImage.setOnClickListener(this);
        imgDOBSelector.setOnClickListener(this);

        UserId = i.getExtras().getInt("UserId");
        byte[] byteArrayBmp = i.getByteArrayExtra("BITMAP_SHARED_KEY");
        bitmap = AddUserActivity.getImage(byteArrayBmp);
        capturedImage = findViewById(R.id.capturedImage);
        capturedImage.setImageBitmap(bitmap);
        Cursor c = myDb.getAlldataUsingId(UserId);

        if (c.moveToFirst()) {
            do {
                etName.setText(c.getString(c.getColumnIndex(myDb.Name)));
                etAge.setText(c.getString(c.getColumnIndex(myDb.Age)));
                etEmail.setText(c.getString(c.getColumnIndex(myDb.Email)));
                etMobile.setText(c.getString(c.getColumnIndex(myDb.Mobile_No)));
                tvDOB.setText(c.getString(c.getColumnIndex(myDb.DOB)));

            } while (c.moveToNext());
        }
        c.close();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonTakePhoto) {
            // //camera stuff
            PackageManager pm = getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getBaseContext(), "Camera not available", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.buttonAddOrUpdate) {
            sName = etName.getText().toString();
            sAge = etAge.getText().toString();
            sEmail = etEmail.getText().toString();
            sMobile = etMobile.getText().toString();
            sDOB = tvDOB.getText().toString();
            if (!sAge.isEmpty())
                valAge = Integer.parseInt(sAge);

            if (sName.isEmpty() || sAge.isEmpty() || sEmail.isEmpty() || sMobile.isEmpty() || sDOB.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Field is left blank", Toast.LENGTH_SHORT).show();
            } else if (!uD.validEmail(sEmail)) {
                etEmail.setError("Enter valid Email");
            } else if (sMobile.length() != 10) {
                etMobile.setError("Enter valid Mobile No.");
            } else if (valAge < 15 || valAge > 120) {
                etAge.setError("Enter age between 15 & 120");
            } else {
                myDb.updateuser(UserId, sName, sAge, sEmail, sMobile, sDOB, AddUserActivity.getBytes(bitmap));
                capturedImage.setImageResource(R.drawable.icon_capture);
                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_LONG).show();
                etName.setText("");
                etAge.setText("");
                etEmail.setText("");
                etMobile.setText("");

            }

        } else if (v.getId() == R.id.buttonCancelIns) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.fabImageSelector) {
            openImageChooser();
        } else if (v.getId() == R.id.imgDobSelector) {
            select_date();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
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

    public void select_date() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        showDialog(AddUserActivity.DATE_PICKER_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case AddUserActivity.DATE_PICKER_ID:
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
