package com.example.sqlite3_project.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.product.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    List<Cart> cartList;
    Context context;
    DatabaseHelper dbHelper;
    private CartViewHolder.OnItemAmountChangedListener onItemAmountChangedListener;
    private OnItemDeleteListener onItemDeleteListener;

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }
    public CartAdapter(Context context, List<Cart> cartList){
        this.cartList = cartList;
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void setOnItemAmountChangedListener(CartViewHolder.OnItemAmountChangedListener listener) {
        this.onItemAmountChangedListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Get the cart item at the specified position
        Cart cartItem = cartList.get(position);
        // Bind data to the ViewHolder
        holder.bind(cartItem.getProduct());

        // Set listener for item deletion
        holder.setOnItemDeleteListener(new CartViewHolder.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                // If the listener is not null, invoke the callback method
                if (onItemDeleteListener != null) {
                    onItemDeleteListener.onItemDelete(position);
                }
            }
        });
    }


    // Interface for item deletion listener
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }
    @Override
    public int getItemCount() {
        return cartList.size();
    }
}

