package com.example.se306project1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.se306project1.R;
import com.example.se306project1.adapters.DetailAdapter;
import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.ProductDatabase;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.CartState;
import com.example.se306project1.utilities.ContextState;
import com.example.se306project1.utilities.StringBuilder;
import com.example.se306project1.utilities.UserState;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.Product;

import java.util.List;
import java.util.Locale;

/**
 * @Description: This is DetailActivity class which used to manage DetailActivity pages
 * @author: Qingyang Li
 * @date: 10/08/2022
 */
public class DetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * @Description: the inner class which is used to retrieve the ui element by id.
     * @author: Qingyang Li
     * @date: 10/08/2022
     */
    class ViewHolder {
        private final ViewPager viewPager = findViewById(R.id.viewPager);
        private final Button likeButton = findViewById(R.id.like_button);
        @SuppressLint("WrongViewCast")
        private final MaterialButton unlikeButton = findViewById(R.id.unlike_button);
        private final TextView name = findViewById(R.id.detail_name_textView);
        private final TextView stock = findViewById(R.id.stockNumber);
        private final TextView price = findViewById(R.id.price);
        private final TextView description = findViewById(R.id.description);
        private final TextView likeCount = findViewById(R.id.like_number);
        private final LinearLayout dots = findViewById(R.id.dots);
    }

    //this is used for the image slide dots
    private Drawable activeDot;
    private Drawable inactiveDot;
    private int dotsCount;
    private ImageView[] sliderDots;

    //get the current product.
    private IProduct product;

    private ViewHolder viewHolder;
    private Drawer drawer;
    private ProductSearcher productSearcher;

    public static void startWithName(String name) {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        Intent intent = new Intent(activity.getBaseContext(), DetailActivity.class);
        intent.putExtra("name", name);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActivityState.getInstance().setCurrentActivity(this);
        ContextState.getInstance().setCurrentContext(getApplicationContext());

        this.product = new Product();
        this.viewHolder = new ViewHolder();
        this.drawer = new Drawer();
        this.productSearcher = new ProductSearcher();
        //get the drawable element from the xml file
        this.activeDot = ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot);
        this.inactiveDot = ContextCompat.getDrawable(getApplicationContext(), R.drawable.inactive_dot);

        //initialise the top bar
        this.drawer.initialise();
        this.productSearcher.initialise();

        //get the specificProduct from the database according to their name
        ProductDatabase db = ProductDatabase.getInstance();
        db.getSpecificProduct(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                product = (IProduct) value;
                renderProductInfo();
            }
        }, getIntent().getStringExtra("name"));


        //set page change listener for the view page and change the dots status;
        this.setViewPager();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserState.getInstance().getCurrentUser() == null) {
            MainActivity.start();
            return;
        }
        this.drawer.initialise();
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

    //add to cart functionality
    public void onAddToCart(View view) {
        if (this.product.getStock() == 0) {
            Toast.makeText(this, "No stock", Toast.LENGTH_SHORT).show();
            return;
        }
        ProductDatabase productDatabase = ProductDatabase.getInstance();
        productDatabase.getSpecificProduct(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                IProduct product = (Product) value;
                addToCartSuccess();
                CartState.getCartState().addToCart(product.toCartProduct());
            }
        }, product.getName());
    }

    private void addToCartSuccess() {
        Toast.makeText(this, "The lego is in your cart now", Toast.LENGTH_SHORT).show();
    }

    public void onToggleLike(View view) {
        view.setVisibility(View.INVISIBLE);
        if (view.getId() == R.id.unlike_button && UserState.getInstance().unlike(product.getName())) {
            this.viewHolder.likeButton.setVisibility(View.VISIBLE);
            String likeCount = new StringBuilder(R.string.like_number)
                    .set("likeCount", Integer.parseInt(viewHolder.likeCount.getText().toString()) - 1)
                    .toString();
            viewHolder.likeCount.setText(likeCount);
        } else if (UserState.getInstance().like(product.getName())) {
            this.viewHolder.unlikeButton.setVisibility(View.VISIBLE);
            String likeCount = new StringBuilder(R.string.like_number)
                    .set("likeCount", Integer.parseInt(viewHolder.likeCount.getText().toString()) + 1)
                    .toString();
            viewHolder.likeCount.setText(likeCount);
        }
    }


    /**
     * @Description: this function is used for adjust the size and layout of the product info in detail
     * pages and set adapter for the view page
     */
    public void renderProductInfo() {
        List<Integer> imageList = product.getImages();
        setViewPagerDots(imageList.size());
        DetailAdapter detailAdapter = new DetailAdapter(imageList);
        viewHolder.viewPager.setAdapter(detailAdapter);
        viewHolder.name.setText(product.getName());
        String stockCount = new StringBuilder(R.string.stock_number)
                .set("stockCount", product.getStock())
                .toString();
        viewHolder.stock.setText(stockCount);
        String priceTag = new StringBuilder(R.string.price_tag)
                .set("price", String.format(Locale.ENGLISH, "%.2f", product.getPrice()))
                .toString();
        viewHolder.price.setText(priceTag);
        viewHolder.description.setText(product.getDescription());
        String likeCount = new StringBuilder(R.string.like_number)
                .set("likeCount", product.getLikesNumber())
                .toString();
        viewHolder.likeCount.setText(likeCount);
        this.setLikeButtonState();
    }

    /**
     * @param : int  the size of the imageList, number of image
     * @Description: render the dots of view pager
     */
    private void setViewPagerDots(int size) {
        dotsCount = size;
        sliderDots = new ImageView[size];
        // set all the dots is inactive dots
        for (int i = 0; i < dotsCount; i++) {
            sliderDots[i] = new ImageView(this);
            sliderDots[i].setImageDrawable(inactiveDot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            viewHolder.dots.addView(sliderDots[i], params);
        }
        //at first, the first dots will set to active dot
        sliderDots[0].setImageDrawable(activeDot);
    }

    /**
     * @Description: set the likes button state, As the different category product have different images after liked
     */
    private void setLikeButtonState() {
        if (product.getCategoryTitle().equals("technic")) {
            this.viewHolder.unlikeButton.setIconResource(R.drawable.technic_icon);
            this.viewHolder.unlikeButton.setIconTintResource(R.color.technic);
        } else if (product.getCategoryTitle().equals("star war")) {
            this.viewHolder.unlikeButton.setIconResource(R.drawable.starwar_icon);
            this.viewHolder.unlikeButton.setIconTintResource(R.color.star_war);
        } else {
            this.viewHolder.unlikeButton.setIconTintResource(R.color.city);
            this.viewHolder.unlikeButton.setIconResource(R.drawable.city_icon);
        }
        if (UserState.getInstance().hasLiked(this.product.getName())) {
            this.viewHolder.unlikeButton.setVisibility(View.VISIBLE);
            this.viewHolder.likeButton.setVisibility(View.INVISIBLE);
        } else {
            this.viewHolder.likeButton.setVisibility(View.VISIBLE);
            this.viewHolder.unlikeButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @Description: set page change listener for the view page and change the dots status;
     */
    private void setViewPager() {
        viewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    sliderDots[i].setImageDrawable(inactiveDot);
                }
                sliderDots[position].setImageDrawable(activeDot);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
