package com.example.sqlite3_project.customer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sqlite3_project.BitmapUtils;
import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProductDetails extends AppCompatActivity {
TextView productName, price, description, categoryName, quantity;
ImageView productImage;
DatabaseHelper dbHelper;
private String productID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        dbHelper = new DatabaseHelper(this);
    // ui elements
        productName = findViewById(R.id.productName);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        categoryName = findViewById(R.id.categoryName);
        productImage = findViewById(R.id.productImage);
        quantity = findViewById(R.id.productQuantity);
    // bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productID = bundle.getString("productID");
            loadProductDetails(productID);
        }
    }
    private void loadProductDetails(String productID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE productID = ?", new String[]{productID});
        if (cursor.moveToFirst()) {
            productName.setText(cursor.getString(cursor.getColumnIndex("productName")));
            price.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("price"))));
            description.setText(cursor.getString(cursor.getColumnIndex("description")));
            quantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("quantity"))));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndex("productImage"));
            if (imageData != null) {
                Bitmap bitmap = BitmapUtils.getImage(imageData);
                productImage.setImageBitmap(bitmap);
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
}