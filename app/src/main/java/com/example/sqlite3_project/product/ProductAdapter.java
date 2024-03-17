package com.example.sqlite3_project.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private List<Product> productList;
    private OnProductClickListener listener;
    private Context context;
    private String userType;
    public ProductAdapter(Context context,List<Product> productList, OnProductClickListener listener, String userType) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.userType = userType;
    }
    public String getUserType(){
        return userType;
    }

    public void filter(String searchText) {
        searchText = searchText.toLowerCase(); // Normalize for case-insensitive search
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(searchText)) {
                filteredList.add(product);
            }
        }
        filterList(filteredList);
    }
    public void filterList(List<Product> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(userType.equals("admin")){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
            return new ProductViewHolder(view, this);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout_user, parent, false);
            return new ProductViewHolder(view, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    if(userType.equals("admin")){
                        listener.onProductClick(product);
                    }
                    else if(userType.equals("customer")){
                        listener.userClickProduct(product);
                    }
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
        void userClickProduct(Product product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

