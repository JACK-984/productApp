package com.example.sqlite3_project.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sqlite3_project.BitmapUtils;
import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.google.android.material.imageview.ShapeableImageView;

public class user_profile extends AppCompatActivity {
Button editButton, backButton;
TextView username, phone,email,password;
ShapeableImageView userImage;
String userID;
DatabaseHelper dbHelper;
ImageView passwordToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        userImage = findViewById(R.id.userImage);
        passwordToggle = findViewById(R.id.passwordToggle);

        // Set initial password visibility state and input type
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordToggle.setImageResource(R.drawable.eye_on);

        editButton = findViewById(R.id.editButton);
        userID = getIntent().getExtras().getString("userID");

        backButton = findViewById(R.id.backButton);
        //backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                Intent intent = new Intent(getApplicationContext(), userActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if(userID != null){
            loadUserDetails(userID);
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("userID",userID);
                Intent intent = new Intent(user_profile.this, editUserProfile.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
                username.setText("Set username");
            }

            // Load phone
            int phoneInt = cursor.getInt(cursor.getColumnIndex("phone"));
            if(phoneInt == 0){
                phone.setText("Set phone");
            }else {
                phone.setText(String.valueOf(phoneInt));
            }


            // Load email
            String emailStr = cursor.getString(cursor.getColumnIndex("email"));
            if (!TextUtils.isEmpty(emailStr)) {
                email.setText(emailStr);
            } else {
                email.setText("Set email");
            }

            // Load password
            String passwordStr = cursor.getString(cursor.getColumnIndex("password"));
            if (!TextUtils.isEmpty(passwordStr)) {
                password.setText(passwordStr);
            } else {
                password.setText("Set password");
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


}