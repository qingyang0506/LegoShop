package com.example.se306project1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.se306project1.R;
import com.example.se306project1.adapters.CartProductAdapter;
import com.example.se306project1.database.CartDatabase;
import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.ProductDatabase;
import com.example.se306project1.models.CartProduct;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.AnimationFactory;
import com.example.se306project1.utilities.CartState;
import com.example.se306project1.utilities.ContextState;
import com.example.se306project1.utilities.StringBuilder;
import com.example.se306project1.utilities.UserState;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

/**
 * @Description: This is cartActivity class which used to manage cartActivity pages
 * @author: Frank Ji
 * @date: 12/08/2022
 */
public class CartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * @Description: the inner class which is used to retrieve the ui element by id.
     * @author: Frank Ji
     * @date: 12/08/2022
     */
    class ViewHolder {
        private final RecyclerView cartProductRecyclerView = findViewById(R.id.cart_product_recyclerview);
        private final TextView totalPriceTextview = findViewById(R.id.total_price_textview);
        private final CheckBox selectAllCheckBox = findViewById(R.id.select_all_checkbox);
        ProgressBar cartProductProgressbar = findViewById(R.id.cart_product_progressbar);
    }

    ViewHolder viewHolder;
    Drawer drawer;
    ProductSearcher productSearcher;

    /**
     * @Description: the method that directs to this page
     */
    public static void start() {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        Intent intent = new Intent(activity.getBaseContext(), CartActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActivityState.getInstance().setCurrentActivity(this);
        ContextState.getInstance().setCurrentContext(getApplicationContext());
        this.viewHolder = new ViewHolder();
        this.drawer = new Drawer();
        this.productSearcher = new ProductSearcher();

        // retrieve the cart product from the database and send to adapter
        fetchCartProducts();

        // initialise the top bar element
        this.drawer.initialise();
        this.productSearcher.initialise();
        this.viewHolder.totalPriceTextview.setText(
                new StringBuilder(R.string.price_tag)
                        .set("price", "0.00")
                        .toString()
        );
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
    protected void onStop() {
        super.onStop();
        CartState.getCartState().uncheckAll();
    }

    /**
     * @Description: retrieve the cart product from the database and send to matching adapter
     */
    public void fetchCartProducts() {
        ProductDatabase db = ProductDatabase.getInstance();
        db.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                List<IProduct> products = (List<IProduct>) value;
                CartDatabase cdb = CartDatabase.getInstance();
                cdb.getUsersCartProducts(new FireStoreCallback() {
                    @Override
                    public <T> void Callback(T value) {
                        List<CartProduct> res = (List<CartProduct>) value;
                        CartState.getCartState().setCartList(res);
                        setAdapter(true);
                    }
                }, UserState.getInstance().getCurrentUser().getUsername(), products);
            }
        });
    }

    /**
     * @param shouldAnimate : a boolean value that means whether the view should be animated
     * @Description: set the adapter for this pages
     */
    public void setAdapter(boolean shouldAnimate) {
        CartProductAdapter cartProductAdapter = new CartProductAdapter(
                CartState.getCartState().getCartProducts(),
                this.viewHolder.totalPriceTextview,
                this.viewHolder.selectAllCheckBox
        );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        // set up the cartProduct RecyclerView
        this.viewHolder.cartProductRecyclerView.setLayoutManager(layoutManager);
        this.viewHolder.cartProductRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.cartProductRecyclerView.setAdapter(cartProductAdapter);
        this.viewHolder.cartProductProgressbar.setVisibility(View.GONE);
        this.viewHolder.cartProductRecyclerView.setVisibility(View.VISIBLE);
        if (shouldAnimate) {
            this.viewHolder.cartProductRecyclerView.startAnimation(
                    new AnimationFactory().getSlideFromLeftAnimation()
            );
        }
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

    /**
     * @Description: the click event handler for selecting all
     * @param view : the checkbox that is next to the text "select all"
     */
    public void onSelectAll(View view) {
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()) {
            CartState.getCartState().checkAll();
        } else {
            CartState.getCartState().uncheckAll();
        }
        setAdapter(false);
        this.viewHolder.totalPriceTextview.setText(
                new StringBuilder(R.string.price_tag)
                        .set("price", CartState.getCartState().getPriceString())
                        .toString()
        );
    }

    /**
     * @param view : View  the view object of event target
     * @Description: The checkout functionality for the checkout button
     */
    public void onCheckOut(View view) {
        CartState.getCartState().checkout();
        Toast.makeText(getApplicationContext(), "Items checked out", Toast.LENGTH_SHORT).show();
        this.fetchCartProducts();
        this.viewHolder.totalPriceTextview.setText(
                new StringBuilder(R.string.price_tag)
                        .set("price", CartState.getCartState().getPriceString())
                        .toString()
        );
    }

}