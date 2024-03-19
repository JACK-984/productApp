package com.example.sqlite3_project.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.R;
import com.example.sqlite3_project.product.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    List<Cart> cartList;
    private CartViewHolder.OnItemAmountChangedListener onItemAmountChangedListener;

    public CartAdapter(List<Cart> cartList){
        this.cartList = cartList;
    }

    public void setOnItemAmountChangedListener(CartViewHolder.OnItemAmountChangedListener listener) {
        this.onItemAmountChangedListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        viewHolder.setOnItemAmountChangedListener(onItemAmountChangedListener); // Set listener for each ViewHolder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cartItem = cartList.get(position);
        Product product = cartItem.getProduct();
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}

