package com.example.se306project1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.se306project1.R;
import com.example.se306project1.adapters.CategoryAdapter;
import com.example.se306project1.adapters.SuggestionAdapter;
import com.example.se306project1.adapters.TopPickAdapter;
import com.example.se306project1.dataproviders.DataProvider;
import com.example.se306project1.models.Category;
import com.example.se306project1.models.Category1;
import com.example.se306project1.models.Category2;
import com.example.se306project1.models.Category3;
import com.example.se306project1.models.ICategory;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.Product;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private List<ICategory> categories;
    private ArrayList<IProduct> topProducts;
    private ArrayList<IProduct> allProducts;

    ViewHolder viewHolder;
    AppBarViewHolder appBarViewHolder;

    SuggestionAdapter suggestionAdapter;

    class ViewHolder {
        private final RecyclerView categoryRecyclerView = findViewById(R.id.category_recycler_view);
        private final RecyclerView topPickRecyclerView = findViewById(R.id.top_pick_product_recycler_view);
    }

    class AppBarViewHolder {
        private final Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        private final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        private final NavigationView navigationView = (NavigationView) findViewById(R.id.app_drawer_navigation);
        private final RecyclerView suggestionListRecycler = (RecyclerView) findViewById(R.id.suggestion_recyclerview);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        this.viewHolder = new ViewHolder();
        this.appBarViewHolder = new AppBarViewHolder();
        this.categories = new ArrayList<>();
        this.topProducts = new ArrayList<>();
        this.allProducts = new ArrayList<>();

        this.fillTopPicks();
        this.fillCategories();
        this.fillProductSearchList();

        this.setAdapter();
        this.setAppBar();
        this.setSuggestionAdapter();
    }

    public void setSuggestionAdapter() {
        this.suggestionAdapter = new SuggestionAdapter(allProducts);
        this.appBarViewHolder.suggestionListRecycler.setAdapter(suggestionAdapter);
        this.appBarViewHolder.suggestionListRecycler.setLayoutManager(
                new LinearLayoutManager(
                        getApplicationContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        this.appBarViewHolder.suggestionListRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public void fillProductSearchList() {
        this.allProducts = DataProvider.getIProductList(10);
    }

    public void setAdapter() {
        TopPickAdapter topPickAdapter = new TopPickAdapter(this.topProducts);
        RecyclerView.LayoutManager topPickLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        this.viewHolder.topPickRecyclerView.setLayoutManager(topPickLayoutManager);
        this.viewHolder.topPickRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.topPickRecyclerView.setAdapter(topPickAdapter);

        CategoryAdapter categoryAdapter = new CategoryAdapter(this.categories);
        RecyclerView.LayoutManager categoryLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        this.viewHolder.categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        this.viewHolder.categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.categoryRecyclerView.setAdapter(categoryAdapter);
    }


    public void fillCategories() {
        this.categories = DataProvider.getICategoryList();
    }

    public void fillTopPicks() {
        this.topProducts = DataProvider.getIProductList(5);
    }

    // click event handler for the menu icon in the app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                appBarViewHolder.drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAppBar() {
        setSupportActionBar(appBarViewHolder.toolbar);
        ActionBar tb = getSupportActionBar();
        tb.setHomeAsUpIndicator(R.drawable.menu);
        tb.setTitle("MyMusic");
        tb.setDisplayHomeAsUpEnabled(true);

        // click event handler for the items in the navigation
        appBarViewHolder.navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        appBarViewHolder.drawerLayout.closeDrawers();
                        appBarViewHolder.drawerLayout.setSelected(true);
                        return true;
                    }
                });

        getSupportActionBar().setTitle(R.string.app_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search with keyword...");
        Context that = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                suggestionAdapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
