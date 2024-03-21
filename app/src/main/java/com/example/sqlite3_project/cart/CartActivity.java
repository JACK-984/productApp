package com.example.sqlite3_project.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.customer.userActivity;
import com.example.sqlite3_project.customer.user_profile;
import com.example.sqlite3_project.product.Product;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemAmountChangedListener, CartAdapter.OnItemDeleteListener{
    RecyclerView cartList;
    CartAdapter cartAdapter;
    List<Cart> oldCartItems; // Add a reference to the old list
    List<Cart> cartItems;
    DatabaseHelper dbHelper;
    TextView totalAmount, itemCost;
    Button backButton;
    String userID;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        userID = getIntent().getExtras().getString("userID");
        userType = getIntent().getExtras().getString("userType");
        // Initialize views and database helper
        cartList = findViewById(R.id.cartList);
        totalAmount = findViewById(R.id.totalAmount);
        itemCost = findViewById(R.id.itemAmountCost);
        dbHelper = new DatabaseHelper(this);

        backButton = findViewById(R.id.backButton);
        //backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("userType",userType);
                Intent intent = new Intent(getApplicationContext(), userActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // Get cart items from the database and set product details
        cartItems = dbHelper.getCartItems(userID);
        oldCartItems = new ArrayList<>(cartItems); // Save a copy of the initial list
        for (Cart cartItem : cartItems) {
            Product product = dbHelper.getProductByID(cartItem.getProductID());
            cartItem.setProduct(product);
        }

        // Set up RecyclerView with LinearLayoutManager and CartAdapter
        cartList.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems);
        cartAdapter.setOnItemDeleteListener(this);
        cartAdapter.setOnItemAmountChangedListener(this);
        cartList.setAdapter(cartAdapter);

        // Calculate and display total amount
        calculateAndDisplayTotalAmount();
    }

    // Override method from OnItemAmountChangedListener interface
    @Override
    public void onItemAmountChanged(int position, int newAmount) {
        // Update the amount for the cart item at the specified position
        Cart cartItem = cartItems.get(position);
        cartItem.setQuantity(newAmount);

        // Update total amount
        calculateAndDisplayTotalAmount();
    }
    // Method to calculate and display total amount
    private void calculateAndDisplayTotalAmount() {
        double total = 0;
        for (Cart cartItem : cartItems) {
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        itemCost.setText(String.format("Total: %.2f$", total));
        totalAmount.setText(String.format("Total: $%.2f$", total));
    }

    // Implement the method for item deletion
    @Override
    public void onItemDelete(int position) {
        // Remove the item from the list
        cartItems.remove(position);
        // Notify the adapter about the item removal
        cartAdapter.notifyItemRemoved(position);
        // Recalculate and display total amount
        calculateAndDisplayTotalAmount();
    }
}
