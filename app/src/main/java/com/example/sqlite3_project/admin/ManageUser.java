package com.example.sqlite3_project.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;

import java.util.List;

public class ManageUser extends AppCompatActivity {
    RecyclerView userList;
    UserAdapter adapter;
    List<User> users;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        dbHelper = new DatabaseHelper(this);
        userList = findViewById(R.id.userList);
        // fetching users from db
        users = dbHelper.getUsers();
        adapter = new UserAdapter(this, users);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(adapter);
    }
}