package com.example.sqlite3_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
    private static final int ADD_CATEGORY_REQUEST_CODE = 1;
    CategoryFragment categoryFragment;
    FrameLayout fragmentContainer;
    ProductAdapter adapter;
    TextView menuEmail;
    MenuItem signOut;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    RecyclerView list;
    List<Product> productList;
    String userID;
    String userType;

    // Declare RecyclerView and Adapter
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private static final int UPLOAD_PRODUCT_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // displaying ID and role
        Bundle bundle = getIntent().getExtras();
        Toast.makeText(this, "userID " + bundle.getString("userID"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "ROLE " + bundle.getString("userType"), Toast.LENGTH_SHORT).show();
        userID = bundle.getString("userID");
        userType = bundle.getString("userType");
        if ("admin".equals(userType)) {
            loadCategoryFragment("All");
        } else {
            loadCategoryFragment("All");
        }
        fragmentContainer = findViewById(R.id.fragmentContainer);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuEmail = navigationView.getHeaderView(0).findViewById(R.id.menuEmail);
        toolbar = findViewById(R.id.topAppBar);
        signOut = navigationView.getMenu().findItem(R.id.sign_out);
        // DISPLAYING CATEGORY
        categoryRecyclerView = findViewById(R.id.categoryList);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Category> categories = dbHelper.getCategories();
        categoryAdapter.loadCategoriesFromDatabase(categories);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle drawer open/close
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
        // changing categories button
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                boolean closeDrawer = false;
                if (id == R.id.sign_out) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    closeDrawer = false;
                }
                if (id == R.id.uploadProduct) {
                    Intent intent = new Intent(MainActivity.this, UploadProduct.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", userID);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, UPLOAD_PRODUCT_REQUEST_CODE); // Start activity for result
                    closeDrawer = true;
                }
                if (closeDrawer) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });
    }
    // Implement the onCategoryClicked method

    //    search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        MenuItem refresh = menu.findItem(R.id.refresh);

        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                // Call refreshProductList method of the categoryFragment
                if (categoryFragment != null) {
                    categoryFragment.refreshProductList();
                    // Refresh the categories
                    DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                    List<Category> categories = dbHelper.getCategories();
                    categoryAdapter.refreshCategories(categories);
                }
                return true; // Return true to indicate that the event has been consumed
            }
        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type to Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchQuery", "Submitted query: " + newText);
                if (categoryFragment != null) {
                    categoryFragment.searchProducts(newText); // Pass the search query to CategoryFragment
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void loadCategoryFragment(String initialCategoryName) {
        categoryFragment = CategoryFragment.newInstance(initialCategoryName, userID, categoryAdapter);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.productFragment, categoryFragment)
                .commit();
    }


    @Override
    public void onCategoryClick(String categoryName) {
        if (userType.equals("admin")) {
            Toast.makeText(this, "Category clicked: " + categoryName, Toast.LENGTH_SHORT).show();
            // Update the categoryName variable with the clicked category name
            String categoryNAME = categoryName;
            // Update the CategoryFragment with the new category name
            categoryFragment = CategoryFragment.newInstance(categoryNAME, userID,categoryAdapter);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.productFragment, categoryFragment)
                    .commit();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_PRODUCT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the product list in CategoryFragment
            if (categoryFragment != null) {
                categoryFragment.refreshProductList();
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                List<Category> categories = dbHelper.getCategories();
                categoryAdapter.refreshCategories(categories);
            }
        }
    }
}
