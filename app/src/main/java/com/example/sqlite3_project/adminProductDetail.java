package com.example.sqlite3_project;


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
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class adminProductDetail extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    private EditText productNameEditText;
    ArrayAdapter<String> spinnerAdapter;
    private EditText productPriceEditText;
    private EditText productDescriptionEditText;
    private EditText productQuantityEditText;
    private Button updateProductButton;
    private Button uploadImageButton;
    private ImageView selectedImage;
    AutoCompleteTextView categoryView;
    Spinner categorySelector;
    private DatabaseHelper dbHelper;
    private String productID;
    Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_detail);

        dbHelper = new DatabaseHelper(this);

        productNameEditText = findViewById(R.id.product_name);
        productPriceEditText = findViewById(R.id.product_price);
        productDescriptionEditText = findViewById(R.id.product_description);
        productQuantityEditText = findViewById(R.id.product_quantity);
        updateProductButton = findViewById(R.id.confirm_button);
        uploadImageButton = findViewById(R.id.uploadImage);
        selectedImage = findViewById(R.id.selectedImage);

        categoryView = findViewById(R.id.categoryView);
        categorySelector = findViewById(R.id.categorySelector);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        // Spinner
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerAdapter.add("Create new category");
        categorySelector.setAdapter(spinnerAdapter);
        loadCategories();
        categoryView.setAdapter(adapter);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productID = bundle.getString("productID");
            loadProductDetails(productID);
        }
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to pick an image from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
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
        // Implement click listeners and image uploading functionality if needed
        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productNameEditText.getText().toString();
                String price = productPriceEditText.getText().toString();
                String description = productDescriptionEditText.getText().toString();
                String categoryName = categoryView.getText().toString();
                String quantityStr = productQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(categoryName) || TextUtils.isEmpty(quantityStr)) {
                    Toast.makeText(adminProductDetail.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = Integer.parseInt(quantityStr);
                // Get the category ID corresponding to the selected category name
                long categoryID = getCategoryID(categoryName);

                if (categoryID == -1) {
                    // If categoryID is -1, it means the category doesn't exist, so add it to the database
                    categoryID = addCategory(categoryName);
                    if (categoryID == -1) {
                        // Failed to add category
                        Toast.makeText(adminProductDetail.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // Convert the selected image to a byte array
                byte[] imageData = null;
                if (selectedImageUri != null) {
                    imageData = getImageData(selectedImageUri);
                }

                // Update the product in the database
                boolean success = updateProduct(productID, name, Double.parseDouble(price), description, categoryID, imageData, quantity);
                if (success) {
                    Toast.makeText(adminProductDetail.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(adminProductDetail.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
                dbHelper.deleteEmptyCategories();
                setResult(RESULT_OK);
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
    // Function to add a new category to the database
    private long addCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("categoryName", categoryName);
        long newRowId = db.insert("categories", null, values);
        db.close();
        return newRowId;
    }
    private boolean updateProduct(String productID, String name, double price, String description, long categoryID, byte[] imageData, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productName", name);
        values.put("price", price);
        values.put("description", description);
        values.put("categoryID", categoryID);
        // Check if new image data is available
        if (imageData != null) {
            values.put("productImage", imageData); // Store image data instead of image URI
        }
        values.put("quantity", quantity);
        int rowsAffected = db.update("products", values, "productID = ?", new String[]{productID});
        db.close();

        // Check if the update was successful
        if (rowsAffected > 0) {
            // Set the result to RESULT_OK and finish the activity
            setResult(RESULT_OK);
            finish();
            return true;
        } else {
            return false;
        }
    }
    private void loadProductDetails(String productID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE productID = ?", new String[]{productID});
        if (cursor.moveToFirst()) {
            productNameEditText.setText(cursor.getString(cursor.getColumnIndex("productName")));
            productPriceEditText.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("price"))));
            productDescriptionEditText.setText(cursor.getString(cursor.getColumnIndex("description")));
            productQuantityEditText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("quantity"))));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndex("productImage"));
            if (imageData != null) {
                Bitmap bitmap = BitmapUtils.getImage(imageData);
                selectedImage.setImageBitmap(bitmap);
            }
        }
        cursor.close();
        db.close();
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
