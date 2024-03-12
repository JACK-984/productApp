package com.example.sqlite3_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private List<Product> productList;
    private OnProductClickListener listener;
    private Context context;
    private String userType;
    public ProductAdapter(Context context,List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void filterList(List<Product> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if(userType == "customer"){
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout_user, parent, false);
//            return new ProductViewHolder(view, this);
//        }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
            return new ProductViewHolder(view, this);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onProductClick(product);
                }
            }
        });
    }
    public void deleteProduct(int position) {
        Product product = productList.get(position);
        String productId = product.getId();
        // Delete the product from the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteProduct(productId);
        // Remove the product from the list
        productList.remove(position);
        // Notify the adapter that the data set has changed
        notifyItemRemoved(position);
    }
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

