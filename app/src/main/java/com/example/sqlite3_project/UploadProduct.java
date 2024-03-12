package com.example.sqlite3_project;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadProduct extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> spinnerAdapter;
    AutoCompleteTextView categoryView;
    Spinner categorySelector;
    TextInputEditText productName, productPrice, productDescription, productQuantity;
    Button uploadImage, confirm;
    ImageView selectedImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        dbHelper = new DatabaseHelper(this);

        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        selectedImage = findViewById(R.id.selectedImage);
        productDescription = findViewById(R.id.product_description);
        productQuantity = findViewById(R.id.product_quantity);
        uploadImage = findViewById(R.id.uploadImage);
        confirm = findViewById(R.id.confirm_button);

        categoryView = findViewById(R.id.categoryView);
        categorySelector = findViewById(R.id.categorySelector);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        categoryView.setAdapter(adapter);

        Toast.makeText(this,"userID" + getIntent().getExtras().getString("userID"),Toast.LENGTH_SHORT).show();
        String adminID = getIntent().getExtras().getString("userID");
        // Spinner
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerAdapter.add("Create new category");
        categorySelector.setAdapter(spinnerAdapter);
        loadCategories();
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        categorySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//             not options selected
                if(!parent.getItemAtPosition(position).toString().equals("Create new category")){
                    categoryView.setVisibility(View.GONE);
                    categoryView.setText(parent.getItemAtPosition(position).toString());
                    categoryView.setFocusable(false);
                    categoryView.setFocusableInTouchMode(false);
                    categoryView.setInputType(InputType.TYPE_NULL);
                }
                else{
                    categoryView.setText("");
                    categoryView.setFocusable(true);
                    categoryView.setFocusableInTouchMode(true);
                    categoryView.setInputType(InputType.TYPE_CLASS_TEXT);
                    categoryView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No implementation needed
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = productName.getText().toString();
                String price = productPrice.getText().toString();
                String description = productDescription.getText().toString();
                String categoryName = categoryView.getText().toString();
                String quantityStr = productQuantity.getText().toString();
                int quantity = Integer.parseInt(quantityStr);
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(categoryName)
                || (TextUtils.isEmpty(quantityStr))) {
                    Toast.makeText(UploadProduct.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert the selected image to a byte array
                byte[] imageData = getImageData(selectedImageUri);

                // Get the category ID corresponding to the selected category name
                long categoryID = getCategoryID(categoryName);

                if (categoryID == -1) {
                    // If categoryID is -1, it means the category doesn't exist, so add it to the database
                    categoryID = addCategory(categoryName);
                    if (categoryID == -1) {
                        // Failed to add category
                        Toast.makeText(UploadProduct.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // Add the product to the database
                long newRowId = addProduct(name, Double.parseDouble(price), description, categoryID, imageData, adminID, quantity);
                if (newRowId != -1) {
                    Toast.makeText(UploadProduct.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadProduct.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
                // After successfully adding the product, set the result to RESULT_OK
                setResult(RESULT_OK);

                // Finish the activity to return to the MainActivity
                finish();
            }
        });
    }
    // Function to get the category ID from the category name
    private long getCategoryID(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT categoryID FROM categories WHERE categoryName = ?", new String[]{categoryName});
        long categoryID = -1; // Initialize with a default value
        if (cursor.moveToFirst()) {
            categoryID = cursor.getLong(cursor.getColumnIndex("categoryID"));
        }
        cursor.close();
        db.close();
        return categoryID;
    }

    // Function to add a product to the database
    private long addProduct(String name, double price, String description, long categoryID, byte[] imageData, String adminID, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productName", name);
        values.put("price", price);
        values.put("description", description);
        values.put("categoryID", categoryID);
        values.put("productImage", imageData); // Store image data instead of image URI
        values.put("adminID", adminID);
        values.put("inStock", quantity > 0); // Set inStock based on quantity
        values.put("quantity", quantity);
        long newRowId = db.insert("products", null, values);
        db.close();
        return newRowId;
    }


    // Function to load existing categories into the spinner
    private void loadCategories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT categoryName FROM categories", null);
        if (cursor.moveToFirst()) {
            do {
                String categoryName = cursor.getString(cursor.getColumnIndex("categoryName"));
                spinnerAdapter.add(categoryName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    // Function to add a new category to the database
    private long addCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("categoryName", categoryName);
        long newRowId = db.insert("categories", null, values);
        db.close();
        return newRowId;
    }
    private byte[] getImageData(Uri uri) {
        try {
            // Get a content resolver to access the image file
            ContentResolver contentResolver = getContentResolver();

            // Open an input stream to read the image data
            InputStream inputStream = contentResolver.openInputStream(uri);

            // Decode the input stream into a Bitmap with the desired dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calculate the sample size to scale down the image while maintaining aspect ratio
            options.inSampleSize = calculateInSampleSize(options, 500, 500);

            // Decode the input stream into a Bitmap with the specified sample size
            options.inJustDecodeBounds = false;
            inputStream.close();
            inputStream = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // Convert the Bitmap to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageData = outputStream.toByteArray();

            // Close the input stream
            inputStream.close();

            // Return the byte array containing the compressed image data
            return imageData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than or equal to the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            selectedImageUri = data.getData();
            // Save the image file to the internal storage
//            saveImageToInternalStorage(selectedImageUri);
            // Display the selected image in your UI (optional)
            selectedImage.setImageURI(selectedImageUri);
        }
    }

}