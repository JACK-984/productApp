package com.example.sqlite3_project;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    Button display;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        display = itemView.findViewById(R.id.categoryNames);
    }

    public void bind(Category category) {
        display.setText(category.getName());
    }
}


