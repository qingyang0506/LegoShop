package com.example.se306project1.utilities;

import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.LikesDatabase;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: This is UserState class which is used for managing the User state.
 * @author: XiaoXiao Zhuang
 * @date: 12/08/2022
 */

public class UserState {

    private static UserState userState = null;

    private final List<String> likedProducts = new ArrayList<>();
    private User currentUser = null;

    private UserState() {
    }

    public static UserState getInstance() {
        if (userState == null) {
            userState = new UserState();
        }
        return userState;
    }

    //get the current User
    public User getCurrentUser() {
        return this.currentUser;
    }

    //logout functionality
    public void logoutCurrentUser() {
        currentUser = null;
        likedProducts.clear();
    }

    //set the current user
    public void setCurrentUser(User currentUser) {
        LikesDatabase likesDatabase = LikesDatabase.getInstance();
        likesDatabase.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                List<IProduct> allProducts = (List<IProduct>) value;
                likesDatabase.getUsersAllLikes(new FireStoreCallback() {
                    @Override
                    public <T> void Callback(T value) {
                        List<IProduct> temp = (List<IProduct>) value;
                        for (IProduct p : temp) {
                            likedProducts.add(p.getName());
                        }
                    }
                }, currentUser.getUsername(), allProducts);
            }
        });
        this.currentUser = currentUser;
    }

    //get the liked product of the user when the user login
    public boolean hasLiked(String productName) {
        for (int i = 0; i < likedProducts.size(); i++) {
            if (likedProducts.get(i).equals(productName)) {
                return true;
            }
        }
        return false;
    }

    public boolean like(String productName) {
        if (hasLiked(productName)) return false;
        LikesDatabase db = LikesDatabase.getInstance();
        db.addProductToLikesList(UserState.getInstance().getCurrentUser().getUsername(), productName);
        likedProducts.add(productName);
        return true;
    }

    public boolean unlike(String productName) {
        if (!hasLiked(productName)) return false;
        LikesDatabase db = LikesDatabase.getInstance();
        db.removeProductFromLikesList(UserState.getInstance().getCurrentUser().getUsername(), productName);
        likedProducts.removeIf(p -> p.equals(productName));
        return true;
    }
}
