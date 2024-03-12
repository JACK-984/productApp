package com.example.sqlite3_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    ImageView productImageView;
    TextView productNameTextView;
    TextView productPriceTextView;
    Button deleteButton;
    ProductAdapter adapter; // Add this field to hold the adapter

    public ProductViewHolder(@NonNull View itemView, ProductAdapter adapter) {
        super(itemView);
        this.adapter = adapter; // Initialize the adapter
        productImageView = itemView.findViewById(R.id.productImage);
        productNameTextView = itemView.findViewById(R.id.productName);
        productPriceTextView = itemView.findViewById(R.id.productPrice);
//        deleteButton = itemView.findViewById(R.id.deleteButton);
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
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    // Call deleteProduct method of ProductAdapter
//                    adapter.deleteProduct(position);
//                }
//            }
//        });
        productNameTextView.setText(product.getName());
        productPriceTextView.setText(String.valueOf(product.getPrice() + " $"));
    }
}
