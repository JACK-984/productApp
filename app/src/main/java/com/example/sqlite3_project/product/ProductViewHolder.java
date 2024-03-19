package com.example.sqlite3_project.product;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    ImageView productImageView;
    TextView productNameTextView;
    TextView productPriceTextView;
    Button deleteButton;
    Button addToCart;
    ProductAdapter adapter; // Add this field to hold the adapter

    public ProductViewHolder(@NonNull View itemView, ProductAdapter adapter) {
        super(itemView);
        this.adapter = adapter; // Initialize the adapter
        productImageView = itemView.findViewById(R.id.productImage);
        productNameTextView = itemView.findViewById(R.id.productName);
        productPriceTextView = itemView.findViewById(R.id.productPrice);
        // Determine whether to show or hide deleteButton based on userType
        if ("admin".equals(adapter.getUserType())) {
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }else {
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }
    public void bind(Product product) {
        // Bind data to views
        try {
            // Convert byte array to Bitmap and set it to ImageView
            byte[] imageData = product.getProductImage(); // Assuming getProductImage() returns byte array
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            productImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            // Log the error or handle it appropriately
            e.printStackTrace();
        }
        // add to cart button
        if("customer".equals(adapter.getUserType())){
            addToCart.setOnClickListener(new View.OnClickListener() {
                int position = getAdapterPosition();
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "add to cart clicked",Toast.LENGTH_SHORT).show();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the product ID and user ID
                        int productID = Integer.parseInt(adapter.getProductID(position));
                        int userID = Integer.parseInt(adapter.getUserID());
                        int quantity = 1; // Assuming quantity is fixed for now

                        // Add the product to the cart using DatabaseHelper
                        DatabaseHelper dbHelper = new DatabaseHelper(view.getContext());
                        dbHelper.addToCart(userID, productID, quantity);

                        // Show a toast message
                        Toast.makeText(view.getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // delete button
        else if ("admin".equals(adapter.getUserType())) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Call deleteProduct method of ProductAdapter
                        adapter.deleteProduct(position);
                    }
                }
            });
        }
        productNameTextView.setText(product.getName());
        productPriceTextView.setText(String.valueOf(product.getPrice() + " $"));
    }
}
