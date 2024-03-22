package com.example.sqlite3_project.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.customer.userActivity;
import com.example.sqlite3_project.login_register.LoginActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    private String userID;
    private String userType;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        dbHelper = new DatabaseHelper(this);
        // Retrieve userID and userType from the intent that started this activity
        userID = getIntent().getStringExtra("userID");
        userType = getIntent().getStringExtra("userType");
        Log.d("PaymentSuccessActivity", "Received userID: " + userID + ", userType: " + userType);

        Button continueShoppingButton = findViewById(R.id.continueShoppingButton);
        continueShoppingButton.setOnClickListener(v -> {
            dbHelper.clearCart(userID);
            // Start userActivity with userID and userType extras
            Intent userIntent = new Intent(PaymentSuccessActivity.this, userActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userID", userID);
            bundle.putString("userType", userType);
            userIntent.putExtras(bundle);
            startActivity(userIntent);
            finish(); // Close the PaymentSuccessActivity
        });
    }
}
