package com.example.se306project1.activities;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se306project1.R;
import com.example.se306project1.adapters.SuggestionAdapter;
import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.ProductDatabase;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.utilities.ActivityState;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: This is ProductSearch class which used for search functionality in the top bar
 * @author: QingyangLi
 * @date: 14/08/2022
 */
public class ProductSearcher {

    static class ViewHolder {
        private RecyclerView suggestionListRecycler;
    }

    private final List<IProduct> productPool;
    private final AppCompatActivity activity;
    private final ViewHolder viewHolder;
    private SuggestionAdapter suggestionAdapter;

    public ProductSearcher() {
        this.activity = ActivityState.getInstance().getCurrentActivity();
        this.viewHolder = new ViewHolder();
        this.viewHolder.suggestionListRecycler = this.activity.findViewById(R.id.suggestion_recyclerview);
        this.productPool = new ArrayList<>();
        this.fillProductSearPool();
    }

    //initialise the searchPool
    public void initialise() {
        this.suggestionAdapter = new SuggestionAdapter(productPool);
        this.viewHolder.suggestionListRecycler.setAdapter(this.suggestionAdapter);
        this.viewHolder.suggestionListRecycler.setLayoutManager(
                new LinearLayoutManager(
                        this.activity.getApplicationContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        this.viewHolder.suggestionListRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public boolean onCreateOptionsMenu(Menu menu, boolean superValue) {
        this.activity.getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search with keyword...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ProductActivity.startWithSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                suggestionAdapter.getFilter().filter(s);
                return false;
            }
        });
        return superValue;
    }


    /**
     * @Description: retrieve the data from database according to the search and fill product search pool
     */
    private void fillProductSearPool() {
        ProductDatabase productDatabase = ProductDatabase.getInstance();
        productDatabase.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                List<IProduct> products = (List<IProduct>) value;
                productPool.addAll(products);
            }
        });
    }
}
