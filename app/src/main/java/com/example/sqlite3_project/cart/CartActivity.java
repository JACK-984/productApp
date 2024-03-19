package com.example.sqlite3_project.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.product.Product;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartViewHolder.OnItemAmountChangedListener{
    RecyclerView cartList;
    CartAdapter cartAdapter;
    List<Cart> cartItems;
    DatabaseHelper dbHelper;
    TextView totalAmount, itemCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views and database helper
        cartList = findViewById(R.id.cartList);
        totalAmount = findViewById(R.id.totalAmount);
        itemCost = findViewById(R.id.itemAmountCost);
        dbHelper = new DatabaseHelper(this);

        // Get cart items from the database and set product details
        cartItems = dbHelper.getCartItems();
        for (Cart cartItem : cartItems) {
            Product product = dbHelper.getProductByID(cartItem.getProductID());
            cartItem.setProduct(product);
        }

        // Set up RecyclerView with LinearLayoutManager and CartAdapter
        cartList.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems);
        cartAdapter.setOnItemAmountChangedListener(this); // Set listener
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
        itemCost.setText(String.format("Total: $%.2f", total));
        totalAmount.setText(String.format("Total: $%.2f", total));
    }
}
