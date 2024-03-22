package com.example.sqlite3_project.admin;

public class User {
    String userID;
    String username;
    String email;
    String password;
    int phone;
    byte[] userImage;

    public User(String userID, String username, String email, int phone, String password, byte[] userImage) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public String getUserID() {
        return userID;
    }
}
