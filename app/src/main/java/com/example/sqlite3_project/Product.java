package com.example.sqlite3_project;

public class Product {
    private String id; // Unique product ID
    private String description;
    private byte[] productImage; // Byte array containing the image data
    private String name;
    private double price;
    private String userID;
    private int categoryID;
    private boolean inStock;
    private int quantity;

    // Constructor
    public Product(String id, String description, byte[] productImage, String name, double price, String userID, int categoryID, boolean inStock, int quantity) {
        this.id = id;
        this.description = description;
        this.productImage = productImage;
        this.name = name;
        this.price = price;
        this.userID = userID;
        this.categoryID = categoryID;
        this.inStock = inStock;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getProductImage() {
        return productImage;
    }

    public void setProductImage(byte[] imageData) {
        this.productImage = productImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}
