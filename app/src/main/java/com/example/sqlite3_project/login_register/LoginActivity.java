package com.example.sqlite3_project.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.admin.MainActivity;
import com.example.sqlite3_project.customer.userActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    ImageView test;
    TextView signUp;
    Button submitBtn;
    MaterialSwitch switchBtn;
    TextInputEditText userName, inputPassword;
    DatabaseHelper dbHelper;
    CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });
        switchBtn = findViewById(R.id.switchBtn);
        userName = findViewById(R.id.userLogin);
        inputPassword = findViewById(R.id.passwordLogin);
        remember = findViewById(R.id.remember);
//        if(remember.isChecked()){
//            retrieveSavedCredentials(); // retrieve saved credentials
//        }
        retrieveSavedCredentials();
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
        submitBtn = findViewById(R.id.button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve username and password entered by the user
                String username = userName.getText().toString().trim();
                String userPassword = inputPassword.getText().toString().trim();

                // Determine the user type based on the switch button state
                final String userType = switchBtn.isChecked() ? "admin" : "customer";
                Bundle bundle = new Bundle();
                // Check the user credentials against the database
                if (verifyCredentials(username, userPassword, userType)) {
                    // Save the credentials if "Remember Me" is checked
                    if (remember.isChecked()) {
                        saveCredentials(username, userPassword, userType);
                    }
                    if(userType == "admin"){
                        // Start the appropriate activity based on the user type
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        bundle.putString("userID",getID(username,userPassword,userType));
                        bundle.putString("userType",userType);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish(); // Close the LoginActivity
                    }else {
                        // Start the appropriate activity based on the user type
                        Intent intent = new Intent(LoginActivity.this, userActivity.class);
                        bundle.putString("userID",getID(username,userPassword,userType));
                        bundle.putString("userType",userType);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish(); // Close the LoginActivity
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to save credentials
    private void saveCredentials(String username, String password, String userType) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("userType", userType);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    // Method to retrieve saved credentials
    // Method to retrieve saved credentials
    // Method to retrieve saved credentials
    // Method to retrieve saved credentials
    private void retrieveSavedCredentials() {
        Log.d("LoginActivity", "Retrieving saved credentials...");
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        Log.d("LoginActivity", "Remember Me option: " + rememberMe);
        remember.setChecked(rememberMe); // Update the checkbox state based on the preference value
        if (rememberMe) {
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            String userType = sharedPreferences.getString("userType", "");
            Log.d("LoginActivity", "Saved credentials retrieved successfully:");
            Log.d("LoginActivity", "Username: " + username);
            Log.d("LoginActivity", "Password: " + password);
            Log.d("LoginActivity", "User type: " + userType);
            // Fill in the username, password, and userType fields with the retrieved values
            userName.setText(username);
            inputPassword.setText(password);
            switchBtn.setChecked(userType.equals("admin"));
        } else {
            Log.d("LoginActivity", "No saved credentials found.");
        }
    }




    // Method to verify user credentials against the database
    private boolean verifyCredentials(String username, String password, String userType) {
        // Create a new instance of DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Get a readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a cursor to execute the query
        Cursor cursor = null;

        try {
            // Determine the query based on the user type
            String query = "";
            if (userType.equals("admin")) {
                query = "SELECT * FROM admin WHERE admin_username = ? AND admin_pw = ?";
            } else {
                query = "SELECT * FROM user WHERE username = ? AND password = ?";
            }
            // Execute the query with username and password as arguments
            cursor = db.rawQuery(query, new String[]{username, password});

            // Check if the cursor has any rows
            return cursor.getCount() > 0;
        } finally {
            // Close the cursor and the database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    private String getID(String username, String password, String userType) {
        // Create a new instance of DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Get a readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a cursor to execute the query
        Cursor cursor = null;

        try {
            // Determine the query based on the user type
            String query = "";
            if (userType.equals("admin")) {
                query = "SELECT adminID FROM admin WHERE admin_username = ? AND admin_pw = ?";
            } else {
                query = "SELECT userID FROM user WHERE username = ? AND password = ?";
            }
            // Execute the query with username and password as arguments
            cursor = db.rawQuery(query, new String[]{username, password});

            // Check if the cursor has any rows
            if (cursor.moveToFirst()) {
                // Get the userID from the cursor
                String userID = cursor.getString(0);
                return userID;
            } else {
                // Return null if no matching credentials found
                return null;
            }
        } finally {
            // Close the cursor and the database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
}

