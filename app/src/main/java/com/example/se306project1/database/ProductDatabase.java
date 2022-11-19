package com.example.se306project1.database;

import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.Product;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: This is ProductDatabase class which used for crud operation for products
 * @date: 18/08/2022
 */
public class ProductDatabase implements IProductDatabase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ProductDatabase productDatabase;
    public static String PRODUCT = "Products";
    public static String CATEGORY_TITLE = "categoryTitle";

    //Singleton mode to retrieve the database object
    public static ProductDatabase getInstance() {
        if (productDatabase == null) {
            productDatabase = new ProductDatabase();
        }
        return productDatabase;
    }

    /**
     * @param productName String
     * @param fieldName   String
     * @param value
     * @param <T>         Generic type
     * @Description: update the product's specific field's value to new value
     * As the product name is key value,therefore,we just need the product name
     */
    public <T> void updateProductInfo(String productName, String fieldName, T value) {
        DocumentReference prod = db.collection(PRODUCT).document(productName);
        prod.update(fieldName, value);
    }

    /**
     * @param fireStoreCallback FireStoreCallback interface used by callback for retrieving data from database
     * @Description: get all products in database
     */
    public void getAllProducts(FireStoreCallback fireStoreCallback) {
        List<IProduct> products = new ArrayList<>();
        db.collection(PRODUCT).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot ds : documents) {
                if (ds.exists()) {
                    IProduct product = ds.toObject(Product.class);
                    products.add(product);
                }
            }
            fireStoreCallback.Callback(products);
        });
    }

    /**
     * @param fireStoreCallback FireStoreCallback
     * @param productName       String
     * @Description: get the specific product in db according to its name using call back
     */
    public void getSpecificProduct(FireStoreCallback fireStoreCallback, String productName) {
        db.collection(PRODUCT).document(productName).get().addOnSuccessListener(documentSnapshot -> {
            IProduct product = documentSnapshot.toObject(Product.class);
            fireStoreCallback.Callback(product);
        });
    }


    /**
     * @param fireStoreCallback FireStoreCallback
     * @param categoryTitle     String
     * @Description: get the all products of specific category according to its title directly from database
     */
    public void getAllProductsByCategoryTitle(FireStoreCallback fireStoreCallback, String categoryTitle) {
        List<IProduct> products = new ArrayList<>();
        db.collection(PRODUCT)
                .whereEqualTo(CATEGORY_TITLE, categoryTitle)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        if (doc.exists()) {
                            IProduct product = doc.toObject(Product.class);
                            products.add(product);
                        }
                    }
                    fireStoreCallback.Callback(products);
                });
    }

    /**
     * @param productName String
     * @param fieldName   String
     * @param step
     * @param <T>
     * @Description: increment the product's field amount by step in database
     */
    public <T> void updateIncrement(String productName, String fieldName, T step) {
        DocumentReference products = db.collection(PRODUCT).document(productName);
        products.update(fieldName, FieldValue.increment((int) step));
    }

}
