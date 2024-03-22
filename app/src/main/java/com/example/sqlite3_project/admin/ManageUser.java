package com.example.sqlite3_project.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.customer.user_profile;

import java.util.List;

public class ManageUser extends AppCompatActivity implements UserAdapter.OnItemClickListener{
    RecyclerView userList;
    UserAdapter adapter;
    List<User> users;
    DatabaseHelper dbHelper;
    Button backButton;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        dbHelper = new DatabaseHelper(this);
        userList = findViewById(R.id.userList);
        // fetching users from db
        users = dbHelper.getUsers();
        adapter = new UserAdapter(this, users,this);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(adapter);
        userID = getIntent().getExtras().getString("userID");
        String userType = getIntent().getExtras().getString("userType");
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userID",userID);
                    String userType = "admin";
                    bundle.putString("userType", userType);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
            }
        });
    }
    // tver ot torn te
    @Override
    public void onItemClick(int position) {
//        User eachUser = users.get(position);
//        String customerID = eachUser.getUserID();
//        String userType = "customer";
//        boolean fromAdmin = true;
//        Bundle bundle = new Bundle();
//        bundle.putString("customerID",customerID);
//        bundle.putString("userType", userType);
//        bundle.putBoolean("fromAdmin", fromAdmin);
//        Intent intent = new Intent(this, user_profile.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        Toast.makeText(this,"UserID" + eachUser.getUserID(),Toast.LENGTH_SHORT).show();
    }
}