package com.example.se306project1.activities;

import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.se306project1.R;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.CartState;
import com.example.se306project1.utilities.StringBuilder;
import com.example.se306project1.utilities.UserState;
import com.google.android.material.navigation.NavigationView;

public class Drawer {

    static class DrawerViewHolder {
        private Toolbar toolbar;
        private DrawerLayout drawerLayout;
        private NavigationView navigationView;
    }

    DrawerViewHolder drawerViewHolder;
    AppCompatActivity activity;

    public Drawer() {
        this.activity = ActivityState.getInstance().getCurrentActivity();
        this.drawerViewHolder = new DrawerViewHolder();
        this.drawerViewHolder.toolbar = this.activity.findViewById(R.id.app_toolbar);
        this.drawerViewHolder.drawerLayout = this.activity.findViewById(R.id.drawerlayout);
        this.drawerViewHolder.navigationView = this.activity.findViewById(R.id.app_drawer_navigation);
    }

    public void initialise() {
        this.activity.setSupportActionBar(this.drawerViewHolder.toolbar);
        ActionBar tb = this.activity.getSupportActionBar();
        assert tb != null;
        tb.setHomeAsUpIndicator(R.drawable.menu);
        tb.setTitle(R.string.app_title);
        tb.setDisplayHomeAsUpEnabled(true);
        (
                (TextView) this.drawerViewHolder.navigationView
                        .getHeaderView(0)
                        .findViewById(R.id.drawer_greet_textview)
        ).setText(new StringBuilder(R.string.greeting)
                .set("username", UserState.getInstance().getCurrentUser().getUsername())
                .toString()
        );
        // click event handler for the items in the navigation
        this.drawerViewHolder.navigationView.setNavigationItemSelectedListener(
                item -> {
                    drawerViewHolder.drawerLayout.closeDrawers();
                    drawerViewHolder.drawerLayout.setSelected(true);
                    return true;
                });
        for (Class<?> c: this.activity.getClass().getInterfaces()) {
            if (c.equals(NavigationView.OnNavigationItemSelectedListener.class)) {
                this.drawerViewHolder.navigationView.setNavigationItemSelectedListener(
                        (NavigationView.OnNavigationItemSelectedListener) this.activity
                );
                break;
            }
        }

    }

    public boolean setUp(MenuItem item, boolean toBeReturned) {
        if (item.getItemId() == android.R.id.home) {
            this.drawerViewHolder.drawerLayout.openDrawer(GravityCompat.START);
        }
        return toBeReturned;
    }

    public boolean onNavigationItemSelected(MenuItem item, boolean toBeReturned) {
        if (item.getItemId() ==  R.id.nav_homepage) {
            CategoryActivity.start(this.activity);
        } else if (item.getItemId() ==  R.id.nav_likes) {
            ProductActivity.startWithLikes(this.activity);
        } else if (item.getItemId() ==  R.id.nav_cart) {
            CartActivity.start(this.activity);
        } else if (item.getItemId() ==  R.id.nav_technic) {
            ProductActivity.startWithTheme(this.activity, "Technic");
        } else if (item.getItemId() ==  R.id.nav_starwar) {
            ProductActivity.startWithTheme(this.activity, "Star War");
        } else if (item.getItemId() ==  R.id.nav_friends) {
            ProductActivity.startWithTheme(this.activity, "City");
        } else if (item.getItemId() ==  R.id.nav_logout) {
            UserState.getInstance().logoutCurrentUser();
            CartState.getCartState().userLogout();
            MainActivity.start(this.activity);
        }
        this.drawerViewHolder.drawerLayout.closeDrawer(GravityCompat.START);
        return toBeReturned;
    }

}
