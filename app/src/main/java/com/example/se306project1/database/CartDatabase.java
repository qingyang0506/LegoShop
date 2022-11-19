package com.example.se306project1.database;


import com.example.se306project1.models.CartProduct;
import com.example.se306project1.models.IProduct;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: This is cartDatabase class which used for crud operation for cart products
 * @date: 17/08/2022
 */
public class CartDatabase extends ProductDatabase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static CartDatabase cartDatabase;
    public static String CART = "cart";
    public static String CART_PROD = "cartproducts";
    public static String CART_NUM = "cartNum";

    //Singleton mode to retrieve the database object
    public static CartDatabase getInstance() {
        if (cartDatabase == null) {
            cartDatabase = new CartDatabase();
        }

        return cartDatabase;
    }

    /**
     * @param userName    String
     * @param productName String
     * @Description: add the product to the user's shopping cart
     */
    public void addProductToCart(String userName, String productName) {
        DocumentReference cart = db.collection(CART).document(userName);
        cart.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                cart.update(CART_PROD, FieldValue.arrayUnion(productName));
            } else {
                //if the document is not exist, we will make a new one and add product into it.
                Map<String, List<String>> map = new HashMap<>();
                List<String> carts = new ArrayList<>();
                carts.add(productName);
                map.put(CART_PROD, carts);
                cart.set(map);
            }
        });
    }

    /**
     * @param userName    String
     * @param productName String
     * @Description: remove the specific product from the user's cart.
     */
    public void removeProductFromCart(String userName, String productName) {
        DocumentReference cart = db.collection(CART).document(userName);
        cart.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                cart.update(CART_PROD, FieldValue.arrayRemove(productName));
                db.collection(CART_NUM).document(userName + "-" + productName).delete();
            }
        });
    }

    /**
     * @param fireStoreCallback FireStoreCallback interface which used for call back
     * @param username          String
     * @param products          List<IProduct>
     * @Description: get the all cart products of this user.
     */
    public void getUsersCartProducts(FireStoreCallback fireStoreCallback, String username, List<IProduct> products) {
        List<CartProduct> ans = new ArrayList<>();
        db.collection(CART).document(username).get().addOnSuccessListener(documentSnapshot -> {
            List<String> carts = (List<String>) documentSnapshot.get(CART_PROD);
            if (carts != null) {
                for (int i = 0; i < products.size(); i++) {
                    if (carts.contains(products.get(i).getName())) {
                        CartProduct cartProduct = products.get(i).toCartProduct();
                        ans.add(cartProduct);
                    }
                }
            }
            fireStoreCallback.Callback(ans);
        });
    }

    /**
     * @param username    String user name
     * @param productName String product name
     * @Description: substract the amount of the cart product in database by 1
     */
    public void subtractCartAmount(String username, String productName) {
        DocumentReference cartNum = db.collection(CART_NUM).document(username + "-" + productName);
        cartNum.get().addOnSuccessListener(documentSnapshot -> cartNum.update("amount", FieldValue.increment(-1)));
    }


}
