package com.example.se306project1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.se306project1.R;
import com.example.se306project1.adapters.CategoryAdapter;
import com.example.se306project1.adapters.TopPickAdapter;
import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.LikesDatabase;
import com.example.se306project1.models.TechnicCategory;
import com.example.se306project1.models.StarWarCategory;
import com.example.se306project1.models.CityCategory;
import com.example.se306project1.models.ICategory;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.AnimationFactory;
import com.example.se306project1.utilities.ContextState;
import com.example.se306project1.utilities.UserState;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: This is CategoryActivity class which used to manage CategoryActivity pages
 * @author: XiaoXiao Zhuang
 * @date: 10/08/2022
 */
public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // This is list is used for the category recyclerview
    private List<ICategory> categories;

    Drawer drawer;
    ProductSearcher productSearcher;
    ViewHolder viewHolder;

    /**
     * @Description: the inner class which is used to retrieve the ui element by id.
     * @author: XiaoXiao Zhuang
     * @date: 10/08/2022
     */
    class ViewHolder {
        private final RecyclerView categoryRecyclerView = findViewById(R.id.category_recycler_view);
        private final RecyclerView topPickRecyclerView = findViewById(R.id.top_pick_product_recycler_view);
        ProgressBar topPickProgressbar = findViewById(R.id.top_pick_progressbar);
    }

    /**
     * @Description: the method that directs to this page
     */
    public static void start() {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        Intent intent = new Intent(activity.getBaseContext(), CategoryActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ActivityState.getInstance().setCurrentActivity(this);
        ContextState.getInstance().setCurrentContext(getApplicationContext());

        this.viewHolder = new ViewHolder();
        this.categories = new ArrayList<>();
        this.drawer = new Drawer();
        this.productSearcher = new ProductSearcher();

        //retrieve top picks and all categories
        this.fillTopPicks();
        this.fillCategories();

        //set up all adapters in this pages and initialise the top bar
        this.setCategoryAdapter();
        this.drawer.initialise();
        this.productSearcher.initialise();

        this.viewHolder.topPickRecyclerView.startAnimation(
                new AnimationFactory().getSlideFromLeftAnimation()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserState.getInstance().getCurrentUser() == null) {
            MainActivity.start();
            return;
        }
        this.fillTopPicks();
        this.drawer.initialise();
    }

    /**
     * @Description: set up the adapter for category recyclerView
     */
    private void setCategoryAdapter() {
        CategoryAdapter categoryAdapter = new CategoryAdapter(this.categories);
        this.viewHolder.categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.categoryRecyclerView.setAdapter(categoryAdapter);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.viewHolder.categoryRecyclerView.setLayoutManager(
                    new GridLayoutManager(this, 2)
            );
        } else {
            this.viewHolder.categoryRecyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                    )
            );
        }
    }

    /**
     * @param list : List<IProduct>  the list of IProduct used for adapter
     * @Description: set up the adapter for top likes product recyclerView
     */
    private void setTopProductAdapter(List<IProduct> list) {
        RecyclerView.LayoutManager topPickLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        this.viewHolder.topPickRecyclerView.setLayoutManager(topPickLayoutManager);
        this.viewHolder.topPickRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.topPickRecyclerView.setAdapter(new TopPickAdapter(list));
        this.viewHolder.topPickProgressbar.setVisibility(View.GONE);
        this.viewHolder.topPickRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * @Description: retrieve three categories
     */
    private void fillCategories() {
        this.categories.add(new TechnicCategory());
        this.categories.add(new StarWarCategory());
        this.categories.add(new CityCategory());
    }

    /**
     * @Description: get the first 4 top likes product from the database
     */
    private void fillTopPicks() {
        int size = 4;
        LikesDatabase likesDatabase = LikesDatabase.getInstance();
        likesDatabase.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                List<IProduct> products = (List<IProduct>) value;
                //sort all products in descending order by the likes number
                products.sort((p1, p2) -> (p2.getLikesNumber() - p1.getLikesNumber()));
                List<IProduct> res = new ArrayList<>(products.subList(0, size));
                setTopProductAdapter(res);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.drawer.setUp(item, super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return this.productSearcher.onCreateOptionsMenu(menu, super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return this.drawer.onNavigationItemSelected(item, true);
    }

}
