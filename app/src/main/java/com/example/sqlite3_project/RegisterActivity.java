package com.example.sqlite3_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText username, password, confirmPassword;
    MaterialSwitch switchBtn;
    Button submitBtn;
    DatabaseHelper dbHelper;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        username = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        switchBtn = findViewById(R.id.switchBtn);
        submitBtn = findViewById(R.id.button);
        login = findViewById(R.id.loginView);

        // switch btn text
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchBtn.isChecked()){
                    switchBtn.setText("Admin");
                }else {
                    switchBtn.setText("Customer");
                }
            }
        });

        // login button that starts Login activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        dbHelper.logUserData();
        dbHelper.logAdminData();
        dbHelper.logCategoryData();
        dbHelper.logProductData();
    }

    private void registerUser() {
        String usernameStr = username.getText().toString().trim();
        String passwordStr = password.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();
        boolean isAdmin = switchBtn.isChecked();
        if(TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passwordStr) || TextUtils.isEmpty(confirmPasswordStr)){
            Toast.makeText(getApplicationContext(),"Please fill in all the fields",Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if passwords match
        if (!passwordStr.equals(confirmPasswordStr)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        try {
            // Get a writable database
            db = dbHelper.getWritableDatabase();

            // Prepare values to insert into the user table
            ContentValues values = new ContentValues();
            if(isAdmin){
                values.put("admin_username", usernameStr);
                values.put("admin_pw", passwordStr);
            }else{
                values.put("username", usernameStr);
                values.put("password", passwordStr);
            }

            // Add userImage if needed
            // values.put("userImage", userImageByteArray); // Replace userImageByteArray with actual image data if available

            long newRowId;
            if (isAdmin) {
                // Insert user data into the admin table
                newRowId = db.insert("admin", null, values);
            } else {
                // Insert user data into the user table
                newRowId = db.insert("user", null, values);
            }

            // Check if the insertion was successful
            if (newRowId != -1) {
                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during database operation
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "An error occurred while registering user", Toast.LENGTH_SHORT).show();
        } finally {
            // Close the database connection in a finally block to ensure it always gets closed
            if (db != null) {
                db.close();
            }
        }
    }

}