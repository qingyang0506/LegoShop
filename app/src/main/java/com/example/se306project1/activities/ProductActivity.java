package com.example.se306project1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.se306project1.R;
import com.example.se306project1.adapters.ProductAdapter;
import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.LikesDatabase;
import com.example.se306project1.database.ProductDatabase;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.Product;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.AnimationFactory;
import com.example.se306project1.utilities.ContextState;
import com.example.se306project1.utilities.ProductActivityState;
import com.example.se306project1.utilities.SortState;
import com.example.se306project1.utilities.UserState;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;


/**
 * @Description: This is ProductActivity class which used to manage productActivity pages
 * @author: Frank Ji
 * @date: 12/08/2022
 */
public class ProductActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static ProductActivityState activityState = ProductActivityState.UNDEFINED;

    //sort status
    private SortState sortState = SortState.NO_SORT;
    private final List<IProduct> defaultOrder = new ArrayList<>();
    private final List<IProduct> products = new ArrayList<>();

    ViewHolder viewHolder;
    Drawer drawer;
    ProductSearcher productSearcher;

    /**
     * @Description: inner class for retrieve the UI element by id
     * @author: Frank Ji
     * @date: 12/08/2022
     */
    class ViewHolder {
        private final RecyclerView productRecyclerView = findViewById(R.id.product_recycler_view);
        private final Button likeSortButton = findViewById(R.id.sort_by_likes_button);
        private final Button likeSortAscButton = findViewById(R.id.sort_by_likes_ascend_button);
        private final Button likeSortDscButton = findViewById(R.id.sort_by_likes_desend_button);
        private final Button priceSortButton = findViewById(R.id.sort_by_price_button);
        private final Button priceSortAscButton = findViewById(R.id.sort_by_price_ascend_button);
        private final Button priceSortDscButton = findViewById(R.id.sort_by_price_descend_button);
        private final TextView noResultTextView = findViewById(R.id.no_search_result_message);
        private final ProgressBar productProgressbar = findViewById(R.id.product_progressbar);
    }

    // this is for three category listActivity
    public static void startWithTheme(String theme) {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        activityState = ProductActivityState.THEME;
        Intent thisIntent = new Intent(activity.getBaseContext(), ProductActivity.class);
        thisIntent.putExtra("theme", theme);
        activity.startActivity(thisIntent);
    }

    //this is for the like product pages
    public static void startWithLikes() {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        activityState = ProductActivityState.LIKE;
        Intent thisIntent = new Intent(activity.getBaseContext(), ProductActivity.class);
        activity.startActivity(thisIntent);
    }

    //this is for the search all product result pages
    public static void startWithSearch(String keyword) {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        activityState = ProductActivityState.SEARCH;
        Intent thisIntent = new Intent(activity.getBaseContext(), ProductActivity.class);
        thisIntent.putExtra("keyword", keyword);
        activity.startActivity(thisIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ActivityState.getInstance().setCurrentActivity(this);
        ContextState.getInstance().setCurrentContext(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (key.equals("theme") && getIntent().getStringExtra("theme") != null) {
                    activityState = ProductActivityState.THEME;
                } else if (key.equals("keyword") && getIntent().getStringExtra("keyword") != null) {
                    activityState = ProductActivityState.SEARCH;
                } else {
                    activityState = ProductActivityState.LIKE;
                }
            }
        }

        this.products.clear();
        this.defaultOrder.clear();

        this.viewHolder = new ViewHolder();
        this.drawer = new Drawer();
        this.productSearcher = new ProductSearcher();

        //initialise the top bar
        this.drawer.initialise();
        this.productSearcher.initialise();

        updateProductList();
        updateSortingButtonStyle();

        this.viewHolder.noResultTextView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserState.getInstance().getCurrentUser() == null) {
            MainActivity.start();
            return;
        }
        this.drawer.initialise();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (key.equals("theme") && getIntent().getStringExtra("theme") != null) {
                    activityState = ProductActivityState.THEME;
                } else if (key.equals("keyword") && getIntent().getStringExtra("keyword") != null) {
                    activityState = ProductActivityState.SEARCH;
                } else {
                    activityState = ProductActivityState.LIKE;
                }
            }
        } else {
            activityState = ProductActivityState.LIKE;
        }
        this.products.clear();
        this.defaultOrder.clear();
        updateProductList();
    }

    //retrieve the product list which will display in this page
    public void updateProductList() {
        if (activityState == ProductActivityState.THEME) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("theme"));
            fetchCategoryProducts(getIntent().getStringExtra("theme").toLowerCase());
        } else if (activityState == ProductActivityState.LIKE) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Your Likes");
            fetchLikedProducts(UserState.getInstance().getCurrentUser().getUsername());
        } else if (activityState == ProductActivityState.SEARCH) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(
                    String.format("Items related to \"%s\"", getIntent().getStringExtra("keyword"))
            );
            String keyword = getIntent().getStringExtra("keyword");
            fetchSearchingResults(keyword);
        }
    }


    /**
     * @Description: set the adapter for product recyclerView, this adapter is used for three category
     * listActivity and also for like pages and search result pages of products
     */
    public void setProductAdapter() {
        ProductAdapter productAdapter = new ProductAdapter(this.products);
        if (activityState == ProductActivityState.SEARCH) {
            String keyword = getIntent().getStringExtra("keyword");
            this.viewHolder.noResultTextView.setVisibility(View.VISIBLE);
            for (IProduct p : this.products) {
                if (p.getName().contains(keyword)) {
                    this.viewHolder.noResultTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        this.viewHolder.productRecyclerView.setLayoutManager(layoutManager);
        this.viewHolder.productRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.productRecyclerView.setAdapter(productAdapter);
        this.viewHolder.productProgressbar.setVisibility(View.GONE);
        this.viewHolder.productRecyclerView.setVisibility(View.VISIBLE);
        this.viewHolder.productRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 2)
        );
        if (activityState == ProductActivityState.THEME) {
            this.viewHolder.productRecyclerView.startAnimation(
                    new AnimationFactory().getSlideFromBottomAnimation()
            );
        } else {
            this.viewHolder.productRecyclerView.startAnimation(
                    new AnimationFactory().getSlideFromLeftAnimation()
            );
        }
    }

    /**
     * @param : String  the category Title  which is used find its product in database
     * @Description: get all specific category product from the database
     */
    public void fetchCategoryProducts(String categoryTitle) {
        this.products.clear();
        ProductDatabase db = ProductDatabase.getInstance();
        db.getAllProductsByCategoryTitle(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                products.clear();
                defaultOrder.clear();
                List<IProduct> res = (List<IProduct>) value;
                products.addAll(res);
                setProductAdapter();
                defaultOrder.addAll(res);
            }
        }, categoryTitle);
    }

    /**
     * @param : String  the keyword  which is used find its result in database
     * @Description: get product list according to the searching
     */
    public void fetchSearchingResults(String keyword) {
        this.products.clear();
        ProductDatabase db = ProductDatabase.getInstance();
        db.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                products.clear();
                defaultOrder.clear();
                List<IProduct> res = (List<IProduct>) value;
                res.removeIf(p -> !p.getName().toLowerCase().contains(keyword.toLowerCase()));
                products.addAll(res);
                setProductAdapter();
                defaultOrder.addAll(res);
            }
        });
    }

    /**
     * @param : String  the username  which is used find its liked products in database
     * @Description: get the product list according to user likes
     */
    public void fetchLikedProducts(String userName) {
        this.products.clear();
        ProductDatabase productDatabase = ProductDatabase.getInstance();
        productDatabase.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                LikesDatabase likesDatabase = LikesDatabase.getInstance();
                likesDatabase.getUsersAllLikes(new FireStoreCallback() {
                    @Override
                    public <T> void Callback(T value) {
                        products.clear();
                        defaultOrder.clear();
                        List<IProduct> likedProducts = (List<IProduct>) value;
                        setProductAdapter();
                        products.addAll(likedProducts);
                        defaultOrder.addAll(likedProducts);
                    }
                }, userName, (List<IProduct>) value);
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

    //this is function is used for sort product list
    public void onSortClick(View view) {
        updateSortState(((Button) view).getText().toString().equalsIgnoreCase("likes"));
        updateSortingButtonStyle();
        sortProductList();
        setProductAdapter();
    }

    //update the sort state(descend, ascend,random)
    private void updateSortState(boolean isLikeClicked) {
        if (isLikeClicked) {
            switch (sortState) {
                case LIKE_DESCEND:
                    sortState = SortState.LIKE_ASCEND;
                    break;
                case LIKE_ASCEND:
                    sortState = SortState.NO_SORT;
                    break;
                default:
                    sortState = SortState.LIKE_DESCEND;
            }
        } else {
            switch (sortState) {
                case PRICE_ASCEND:
                    sortState = SortState.PRICE_DESCEND;
                    break;
                case PRICE_DESCEND:
                    sortState = SortState.NO_SORT;
                    break;
                default:
                    sortState = SortState.PRICE_ASCEND;
            }
        }
    }

    private void updateSortingButtonStyle() {
        this.viewHolder.likeSortButton.setVisibility(View.INVISIBLE);
        this.viewHolder.likeSortAscButton.setVisibility(View.INVISIBLE);
        this.viewHolder.likeSortDscButton.setVisibility(View.INVISIBLE);
        this.viewHolder.priceSortButton.setVisibility(View.INVISIBLE);
        this.viewHolder.priceSortAscButton.setVisibility(View.INVISIBLE);
        this.viewHolder.priceSortDscButton.setVisibility(View.INVISIBLE);
        switch (sortState) {
            case NO_SORT:
                this.viewHolder.likeSortButton.setVisibility(View.VISIBLE);
                this.viewHolder.priceSortButton.setVisibility(View.VISIBLE);
                break;
            case LIKE_ASCEND:
                this.viewHolder.likeSortAscButton.setVisibility(View.VISIBLE);
                this.viewHolder.priceSortButton.setVisibility(View.VISIBLE);
                break;
            case LIKE_DESCEND:
                this.viewHolder.likeSortDscButton.setVisibility(View.VISIBLE);
                this.viewHolder.priceSortButton.setVisibility(View.VISIBLE);
                break;
            case PRICE_ASCEND:
                this.viewHolder.likeSortButton.setVisibility(View.VISIBLE);
                this.viewHolder.priceSortAscButton.setVisibility(View.VISIBLE);
                break;
            case PRICE_DESCEND:
                this.viewHolder.likeSortButton.setVisibility(View.VISIBLE);
                this.viewHolder.priceSortDscButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sortProductList() {
        switch (sortState) {
            case NO_SORT:
                this.products.clear();
                this.products.addAll(defaultOrder);
                break;
            case LIKE_ASCEND:
                this.sortProducts("likesNum", true);
                break;
            case LIKE_DESCEND:
                this.sortProducts("likesNum", false);
                break;
            case PRICE_ASCEND:
                this.sortProducts("price", true);
                break;
            case PRICE_DESCEND:
                this.sortProducts("price", false);
                break;
        }
    }

    //sort product according to the specific fieldName
    private void sortProducts(String fieldName, boolean ascend) {
        this.products.sort((productA, productB) -> {
            Class<Product> productClass = Product.class;
            try {
                Field field = productClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return new BigDecimal(Objects.requireNonNull(field.get(productA)).toString())
                        .subtract(new BigDecimal(Objects.requireNonNull(field.get(productB)).toString()))
                        .intValue() * (ascend ? 1 : -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

}
