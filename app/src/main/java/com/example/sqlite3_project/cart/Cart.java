package com.example.sqlite3_project.cart;

import com.example.sqlite3_project.product.Product;

public class Cart {
    private String cartID;
    private String userID;
    private String productID;
    private int quantity;
    private Product product;
    public Cart(String cartID, String userID, String productID, int quantity, Product product) {
        this.cartID = cartID;
        this.userID = userID;
        this.productID = productID;
        this.quantity = quantity;
        this.product = product;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product){
        this.product = product;
    }
    public String getCartID() {
        return cartID;
    }

    public String getUserID() {
        return userID;
    }

    public String getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }
}

