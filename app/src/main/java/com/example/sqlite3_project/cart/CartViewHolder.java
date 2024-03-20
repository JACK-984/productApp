package com.example.sqlite3_project.cart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.R;
import com.example.sqlite3_project.product.Product;

import org.w3c.dom.Text;

public class CartViewHolder extends RecyclerView.ViewHolder {
    ImageView productImage;
    TextView productName, price, itemAmount;
    ImageView increase, decrease, deleteButton;
    int amount;
    // Listener for item deletion
    OnItemDeleteListener onItemDeleteListener;
    OnItemAmountChangedListener onItemAmountChangedListener; // Declare the listener


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.productImage);
        productName = itemView.findViewById(R.id.productName);
        price = itemView.findViewById(R.id.price);
        increase = itemView.findViewById(R.id.increase);
        decrease = itemView.findViewById(R.id.decrease);
        itemAmount = itemView.findViewById(R.id.itemAmount);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        amount = 1;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemDeleteListener != null) {
                    onItemDeleteListener.onItemDelete(getAdapterPosition());
                }
            }
        });
    }

    public void bind(Product product) {
        byte[] imageData = product.getProductImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        productImage.setImageBitmap(bitmap);
        productName.setText(product.getName());
        price.setText(String.valueOf(product.getPrice()) + "$");
        itemAmount.setText(String.valueOf(amount)); // Set initial amount

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int maxQuantity = product.getQuantity();
                if (amount < maxQuantity) {
                    amount++;
                    itemAmount.setText(String.valueOf(amount));
                    if (onItemAmountChangedListener != null) {
                        onItemAmountChangedListener.onItemAmountChanged(getAdapterPosition(), amount);
                    }
                } else {
                    Toast.makeText(view.getContext(), "Cannot exceed quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount > 1) {
                    amount--;
                    itemAmount.setText(String.valueOf(amount));
                    if (onItemAmountChangedListener != null) {
                        onItemAmountChangedListener.onItemAmountChanged(getAdapterPosition(), amount);
                    }
                }
            }
        });
    }


    // Setter method for the listener
    public void setOnItemAmountChangedListener(OnItemAmountChangedListener listener) {
        this.onItemAmountChangedListener = listener;
    }
    // Setter method for the listener
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    // Interface for item deletion listener
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }
    // Interface for item amount change listener
    public interface OnItemAmountChangedListener {
        void onItemAmountChanged(int position, int newAmount);
    }
}

