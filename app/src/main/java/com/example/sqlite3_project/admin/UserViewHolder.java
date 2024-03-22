package com.example.sqlite3_project.admin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.shapes.Shape;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite3_project.R;
import com.google.android.material.imageview.ShapeableImageView;


public class UserViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    ShapeableImageView userImage;
    UserAdapter.OnItemClickListener listener;
    public UserViewHolder(@NonNull View itemView, UserAdapter.OnItemClickListener listener) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        userImage = itemView.findViewById(R.id.userImage);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(pos);
                    }
                }
            }
        });
    }
    public void bind(User user){
        username.setText(user.getUsername());
        if(user.getUserImage() != null){
            byte[] imageData = user.getUserImage(); // Assuming getProductImage() returns byte array
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            userImage.setImageBitmap(bitmap);
        }
    }
}
