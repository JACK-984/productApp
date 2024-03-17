package com.example.sqlite3_project.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.sqlite3_project.cart.Cart;
import com.example.sqlite3_project.category.Category;
import com.example.sqlite3_project.category.CategoryAdapter;
import com.example.sqlite3_project.category.CategoryFragment;
import com.example.sqlite3_project.DatabaseHelper;
import com.example.sqlite3_project.login_register.LoginActivity;
import com.example.sqlite3_project.R;
import com.example.sqlite3_project.product.ViewPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class userActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    CategoryAdapter categoryAdapter;
    String userID;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toast.makeText(this,"userID " + getIntent().getExtras().getString("userID"),Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"userRole " + getIntent().getExtras().getString("userType"),Toast.LENGTH_SHORT).show();
        userID = getIntent().getExtras().getString("userID");
        userType =  getIntent().getExtras().getString("userType");
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.topAppBar);
        CategoryFragment categoryFragment = new CategoryFragment();
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Category> categories = dbHelper.getCategories();

        // Create a ViewPager
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),1);
        categoryAdapter = new CategoryAdapter(categories,this);
        for (Category category : categories) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(category.getName()); // Assuming getName() returns the name of the category
            tabLayout.addTab(tab);

            // Add a corresponding fragment to the ViewPager
            pagerAdapter.addFragment(CategoryFragment.newInstance(category.getName(), userID, categoryAdapter,userType),category.getName());
        }
// Set up the ViewPager with the adapter
        viewPager.setAdapter(pagerAdapter);

// Connect the TabLayout and ViewPager
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                String categoryName = categories.get(position).getName();
// Remove previous fragment if it exists
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment existingFragment = fragmentManager.findFragmentByTag("category_" + categoryName);
                if (existingFragment != null) {
                    transaction.remove(existingFragment);
                }
                // Add new fragment
                transaction.add(R.id.viewPager, CategoryFragment.newInstance(categoryName, userID, categoryAdapter,userType), "category_" + categoryName);
                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.cart){
                    Log.d("CartMenu", "Cart menu item clicked"); // Add logging statement
                    Intent intent = new Intent(userActivity.this, Cart.class);
                    startActivity(intent);
                    return true;
                }
                if(item.getItemId() == R.id.searchIcon){

                }
                return false;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                boolean closeDrawer = false;
                if (id == R.id.sign_out) {
                    startActivity(new Intent(userActivity.this, LoginActivity.class));
                    finish();
                    closeDrawer = false;
                }
                if (closeDrawer) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

    }
    // METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type to Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void searchProducts(String searchText) {
        // 1. Get the currently active CategoryFragment
        Log.d("SearchQuery", "Search query: " + searchText);
        TabLayout.Tab currentTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("category_" + currentTab.getText());

        if (currentFragment instanceof CategoryFragment) {
            ((CategoryFragment) currentFragment).filterProducts(searchText);
        }
    }
    @Override
    public void onCategoryClick(String categoryName) {

    }
}