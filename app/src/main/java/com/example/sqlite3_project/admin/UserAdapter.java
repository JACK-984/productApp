package com.example.sqlite3_project.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    List<User> users;
    Context context;
    DatabaseHelper dbHelper;
    private OnItemClickListener listener;

    // Interface for item click callback
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public UserAdapter(Context context, List<User> users, OnItemClickListener listener){
        this.context = context;
        this.users = users;
        this.listener = listener;
        dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_layout, parent, false);

        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User eachUser = users.get(position);
        holder.bind(eachUser); // Update ViewHolder with user data
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

