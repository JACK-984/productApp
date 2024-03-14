package com.example.sqlite3_project;

import android.icu.text.Transliterator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    List<Category> categoryList;
    OnCategoryClickListener listener;
    public void refreshCategories(List<Category> newCategories) {
        categoryList.clear(); // Clear existing categories
        categoryList.addAll(newCategories); // Add new categories
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }
    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout,parent,false);
        return new CategoryViewHolder(view);
    }
    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }
    public void loadCategoriesFromDatabase(List<Category> categories) {
        categoryList.clear(); // Clear existing categories
        categoryList.addAll(categories); // Add new categories
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);

        // Set click listener for the button
        holder.display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategoryClick(category.getName());
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}




