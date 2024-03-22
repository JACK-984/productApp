package com.example.sqlite3_project.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sqlite3_project.BitmapUtils;
import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.admin.UploadProduct;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class editUserProfile extends AppCompatActivity {
    String userID;
    ShapeableImageView userImage;
    EditText username, email, password, phone;
    Button uploadImage, confirm, backButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;
    DatabaseHelper dbHelper;
    ImageView passwordToggle;
    boolean fromAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        dbHelper = new DatabaseHelper(this);
        userID = getIntent().getExtras().getString("userID");
        userImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        passwordToggle = findViewById(R.id.passwordToggle);
        // Set initial password visibility state and input type
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordToggle.setImageResource(R.drawable.eye_on);
        backButton = findViewById(R.id.backButton);
        fromAdmin = getIntent().getExtras().getBoolean("fromAdmin");
        //backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fromAdmin){
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", userID);
                    Intent intent = new Intent(getApplicationContext(), user_profile.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", userID);
                    bundle.putBoolean("fromAdmin",fromAdmin);
                    Intent intent = new Intent(getApplicationContext(), user_profile.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        // password toggle

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide the password
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordToggle.setImageResource(R.drawable.eye_on);
                } else {
                    // Show the password
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(null);
                    passwordToggle.setImageResource(R.drawable.eye_off);
                }
                // Toggle the visibility state
                isPasswordVisible = !isPasswordVisible;
            }
        });

        if (userID != null) {
            loadUserDetails(userID);
        }
        // buttons
        uploadImage = findViewById(R.id.uploadImage);
        confirm = findViewById(R.id.confirm);
        // upload image
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        // confirm
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameStr = username.getText().toString();
                String emailStr = email.getText().toString();
                String pwStr = password.getText().toString();
                String phoneStr = phone.getText().toString();

                // Convert the selected image to a byte array
                byte[] imageData = getImageData(selectedImageUri);

                long updatedRow = updateUser(userID, usernameStr, pwStr, phoneStr, emailStr, imageData);
                if (updatedRow != -1) {
                    Toast.makeText(editUserProfile.this, "Update successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(editUserProfile.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                Intent intent = new Intent(getApplicationContext(), user_profile.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private int updateUser(String userID, String username, String password, String phone, String email, byte[] imageData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check each field individually and update only if not null or empty
        if (!TextUtils.isEmpty(username)) {
            values.put("username", username);
        }
        if (!TextUtils.isEmpty(email)) {
            values.put("email", email);
        }
        if (!TextUtils.isEmpty(phone)) {
            values.put("phone", Integer.parseInt(phone));
        }
        if (!TextUtils.isEmpty(password)) {
            values.put("password", password);
        }
        // Assuming userImage is updated too, replace userID with the new imageData
        if (imageData != null) {
            values.put("userImage", imageData);
        }
        String selection = "userID = ?";
        String[] selectionArgs = {userID};

        int rowsUpdated = db.update("user", values, selection, selectionArgs);
        db.close();
        return rowsUpdated;
    }



    private byte[] getImageData(Uri uri) {
        try {
            // If the URI is null, get the image data from the userImage ShapeableImageView
            if (uri == null) {
                BitmapDrawable drawable = (BitmapDrawable) userImage.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    if (bitmap != null) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        return outputStream.toByteArray();
                    }
                }
                return null;
            }

            // Get a content resolver to access the image file
            ContentResolver contentResolver = getContentResolver();
            // Open an input stream to read the image data
            InputStream inputStream = contentResolver.openInputStream(uri);
            // Decode the input stream into a Bitmap with the desired dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            // Calculate the sample size to scale down the image while maintaining aspect ratio
            options.inSampleSize = calculateInSampleSize(options, 500, 500);
            // Decode the input stream into a Bitmap with the specified sample size
            options.inJustDecodeBounds = false;
            inputStream.close();
            inputStream = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            // Convert the Bitmap to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageData = outputStream.toByteArray();
            // Close the input stream
            inputStream.close();
            // Return the byte array containing the compressed image data
            return imageData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadUserDetails(String userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user where userID = ?", new String[]{userID});
        if (cursor.moveToFirst()) {
            // Load username
            String usernameStr = cursor.getString(cursor.getColumnIndex("username"));
            if (!TextUtils.isEmpty(usernameStr)) {
                username.setText(usernameStr);
            } else {
                username.setHint("Set username");
            }

            // Load phone
            int phoneInt = cursor.getInt(cursor.getColumnIndex("phone"));
            if(phoneInt == 0){

                phone.setHint("Set phone");
            }else {
                phone.setText(String.valueOf(phoneInt));
            }


            // Load email
            String emailStr = cursor.getString(cursor.getColumnIndex("email"));
            if (!TextUtils.isEmpty(emailStr)) {
                email.setText(emailStr);
            } else {
                email.setHint("Set email");
            }

            // Load password
            String passwordStr = cursor.getString(cursor.getColumnIndex("password"));
            if (!TextUtils.isEmpty(passwordStr)) {
                password.setText(passwordStr);
            } else {
                password.setHint("Set password");
            }

            // Load image data
            byte[] imageData = cursor.getBlob(cursor.getColumnIndex("userImage"));
            if (imageData != null) {
                Bitmap bitmap = BitmapUtils.getImage(imageData);
                if (bitmap != null) {
                    userImage.setImageBitmap(bitmap);
                } else {
                    // Handle case when bitmap is null
                }
            } else {
                // Handle case when image data is null
                // For example, set a placeholder image or hide the ImageView
            }
        }
        cursor.close();
        db.close();
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than or equal to the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // sets the image to the selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            selectedImageUri = data.getData();
            // Save the image file to the internal storage
//            saveImageToInternalStorage(selectedImageUri);
            // Display the selected image in your UI (optional)
            userImage.setImageURI(selectedImageUri);
        }
    }
}