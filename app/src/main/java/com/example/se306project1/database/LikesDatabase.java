package com.example.se306project1.database;

import com.example.se306project1.models.IProduct;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: This is LikeDatabase class which used for crud operation for likes products
 * @date: 18/08/2022
 */
public class LikesDatabase extends ProductDatabase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static LikesDatabase likesDatabase;
    public static String LIKES = "likes";
    public static String LIKE_LIST = "likeList";
    public static String LIKE_NUM = "likesNumber";

    //Singleton mode to retrieve the database object
    public static LikesDatabase getInstance() {
        if (likesDatabase == null) {
            likesDatabase = new LikesDatabase();
        }
        return likesDatabase;
    }

    /**
     * @param userName    String
     * @param productName String
     * @Description: this function add the product to the user's likes list
     */
    public void addProductToLikesList(String userName, String productName) {
        DocumentReference likes = db.collection(LIKES).document(userName);
        likes.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                likes.update(LIKE_LIST, FieldValue.arrayUnion(productName));
            } else {
                Map<String, List<String>> map = new HashMap<>();
                List<String> likelist = new ArrayList<>();
                likelist.add(productName);
                map.put(LIKE_LIST, likelist);
                likes.set(map);
            }
        });
        ProductDatabase productDatabase = ProductDatabase.getInstance();
        productDatabase.updateIncrement(productName, LIKE_NUM, 1);
    }

    /**
     * @param userName    String
     * @param productName String
     * @Description: remove the product from the user's like list
     */
    public void removeProductFromLikesList(String userName, String productName) {
        DocumentReference likes = db.collection(LIKES).document(userName);
        likes.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                likes.update(LIKE_LIST, FieldValue.arrayRemove(productName));
            }
        });
        ProductDatabase productDatabase = ProductDatabase.getInstance();
        productDatabase.updateIncrement(productName, LIKE_NUM, -1);
    }


    /**
     * @param fireStoreCallback fireStoreCallback interface used by callback for retrieve data from database
     * @param username          String
     * @param products          String
     * @Description: retrieve all likes products of this user
     */
    public void getUsersAllLikes(FireStoreCallback fireStoreCallback, String username, List<IProduct> products) {
        List<IProduct> tt = new ArrayList<>();
        db.collection(LIKES).document(username).get().addOnSuccessListener(documentSnapshot -> {
            List<String> likeList = (List<String>) documentSnapshot.get(LIKE_LIST);
            if (likeList != null) {
                for (int i = 0; i < products.size(); i++) {
                    if (likeList.contains(products.get(i).getName())) {
                        tt.add(products.get(i));
                    }
                }
            }
            fireStoreCallback.Callback(tt);
        });
    }

}
