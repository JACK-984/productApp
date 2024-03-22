package com.example.sqlite3_project.admin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.R;


public class UserViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
    }
    public void bind(User user){
        username.setText(user.getUsername());
    }
}
