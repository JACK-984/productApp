package com.example.sqlite3_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sqlite3_project.category.Category;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE = "product.db";
    static final int DB_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user (userID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, phone INTEGER, password TEXT, userImage BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS products (productID INTEGER PRIMARY KEY AUTOINCREMENT, productName TEXT, price REAL,inStock BOOLEAN,quantity INTEGER, description TEXT, categoryID INTEGER, adminID INTEGER,productImage BLOB, FOREIGN KEY (adminID) REFERENCES admin(adminID), FOREIGN KEY (categoryID) REFERENCES categories(categoryID))");
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (categoryID INTEGER PRIMARY KEY AUTOINCREMENT, categoryName TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS admin (adminID INTEGER PRIMARY KEY AUTOINCREMENT, admin_username TEXT, admin_email TEXT, admin_pw TEXT)");
        // pay
        db.execSQL("CREATE TABLE IF NOT EXISTS pay (\n" +
                "    paymentID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    adminID INTEGER,\n" +
                "    amount REAL,\n" +
                "    paymentDate DATE,\n" +
                "    FOREIGN KEY (adminID) REFERENCES admin(adminID)\n" +
                ");");
        // order
        db.execSQL("CREATE TABLE IF NOT EXISTS orders (\n" +
                "    orderID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    userID INTEGER,\n" +
                "    productID INTEGER,\n" +
                "    quantity INTEGER,\n" +
                "    orderDate DATE,\n" +
                "    totalAmount REAL,\n" +
                "    FOREIGN KEY (userID) REFERENCES user(userID),\n" +
                "    FOREIGN KEY (productID) REFERENCES products(productID)\n" +
                ");\n");
        // cart
        db.execSQL("CREATE TABLE IF NOT EXISTS cart (cartID INTEGER PRIMARY KEY AUTOINCREMENT, userID INTEGER, productID INTEGER, quantity INTEGER, FOREIGN KEY (userID) REFERENCES user(userID),FOREIGN KEY (productID) REFERENCES products(productID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS user");
//        db.execSQL("DROP TABLE IF EXISTS products");
//        db.execSQL("DROP TABLE IF EXISTS categories");
        onCreate(db);
    }
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query to fetch categories from the database
            String query = "SELECT * FROM categories";
            cursor = db.rawQuery(query, null);

            // Iterate through the cursor and add categories to the list
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int categoryId = cursor.getInt(cursor.getColumnIndex("categoryID"));
                    String categoryName = cursor.getString(cursor.getColumnIndex("categoryName"));

                    // Create a Category object and add it to the list
                    Category category = new Category(categoryId, categoryName);
                    categories.add(category);

                    // Log the retrieved category
//                    Log.d("CategoryDebug", "Category ID: " + categoryId + ", Name: " + categoryName);
                } while (cursor.moveToNext());
            }
        } finally {
            // Close the cursor and database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return categories;
    }

    // Method to log data from the 'user' table
    public void logUserData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user", null);
        logDataFromCursor(cursor);
    }
    public void deleteProduct(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "productID = ?", new String[]{productId});
        db.close();
    }
    // Method to log data from the 'products' table
//    public void logProductData() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM products", null);
//        logDataFromCursor(cursor);
//    }
    public void addAllCategory() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if "All" category already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM categories WHERE categoryName = ?", new String[]{"All"});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        // If "All" category doesn't exist, insert it
        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put("categoryName", "All");
            db.insert("categories", null, values);
        }

        db.close();
    }

    // Method to log data from the 'categories' table
    public void logCategoryData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories", null);
        logDataFromCursor(cursor);
    }
    public void logProductData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT productID, productName, description, price, categoryID,inStock,quantity FROM products", null);
        logDataFromCursor(cursor);
    }
    // Method to log data from the 'admin' table
    public void logAdminData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM admin", null);
        logDataFromCursor(cursor);
    }
    public void deleteEmptyCategories() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Query to find categories with no associated products and not named "All"
            String query = "DELETE FROM categories WHERE categoryID NOT IN (SELECT DISTINCT categoryID FROM products) AND categoryName <> 'All'";
            db.execSQL(query);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error deleting empty categories: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    // Helper method to log data from a cursor
    private void logDataFromCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        stringBuilder.append(cursor.getColumnName(i)).append(": ").append(cursor.getString(i)).append(", ");
                    }
                    Log.d("DatabaseDebug", stringBuilder.toString());
                }
            } finally {
                cursor.close();
            }
        }
    }
}
