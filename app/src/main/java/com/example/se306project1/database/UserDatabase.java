package com.example.se306project1.database;


import com.example.se306project1.models.User;

import com.example.se306project1.utilities.PasswordEncripter;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: This is UserDatabase class which used for operations for users
 * @author: XiaoXiao Zhuang
 * @date: 18/08/2022
 */
public class UserDatabase implements IUserDatabase {
    private static UserDatabase userDatabase = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String USER = "Users";


    private UserDatabase() {
    }

    //Singleton mode to retrieve the database object
    public static UserDatabase getInstance() {
        if (userDatabase == null) {
            userDatabase = new UserDatabase();
        }
        return userDatabase;
    }

    /**
     * @param fireStoreCallback FireStoreCallback
     * @param username          String
     * @Description: check if the user exist in database.
     */
    public void isUserExist(FireStoreCallback fireStoreCallback, String username) {
        DocumentReference docRef = db.collection(USER).document(username);
        docRef.get().addOnSuccessListener(documentSnapshot -> fireStoreCallback.Callback(documentSnapshot.exists()));
    }

    /**
     * @param username String
     * @param password String
     * @Description: Add a new user to database
     */
    public void addUserToFireStore(String username, String password) {
        User user = new User(username, password);
        db.collection(USER).document(username).set(user);

        Map<String, List<String>> map = new HashMap<>();
        map.put(LikesDatabase.LIKE_LIST, new ArrayList<>());
        db.collection(LikesDatabase.LIKES).document(username).set(map);
        Map<String, List<String>> map1 = new HashMap<>();
        map1.put(CartDatabase.CART_PROD, new ArrayList<>());
        db.collection(CartDatabase.CART).document(username).set(map1);
    }

    /**
     * @param fireStoreCallback FireStoreCallback
     * @param username          String
     * @param password          String
     * @Description: check whether is a valid login
     */
    public void isLoginValid(FireStoreCallback fireStoreCallback, String username, String password) {
        DocumentReference docRef = db.collection(USER).document(username);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                boolean isValid = Objects.requireNonNull(
                                documentSnapshot.toObject(User.class))
                        .getPassword()
                        .equals(PasswordEncripter.hashPassword(password)
                        );
                fireStoreCallback.Callback(isValid);
            }
        });
    }
}