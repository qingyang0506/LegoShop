package com.example.se306project1.database;

import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.Product;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabase implements IProductDatabase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ProductDatabase productDatabase;

    public static ProductDatabase getInstance() {
        if (productDatabase == null) {
            productDatabase = new ProductDatabase();
        }
        return productDatabase;
    }

    //update the product's specific field's value to new value
    //As the product name is key value,therefore,we just need the product name
    public <T> void updateProductInfo(String productName, String fieldName, T value) {
        DocumentReference prod = db.collection("Products").document(productName);
        prod.update(fieldName, value);
    }

    public void getAllProducts(FireStoreCallback fireStoreCallback) {
        List<IProduct> products = new ArrayList<>();
        db.collection("Products").get().addOnSuccessListener(queryDocumentSnapshots -> {
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

    //get the specific product in db according to its name using call back
    public void getSpecificProduct(FireStoreCallback fireStoreCallback, String productName) {
        db.collection("Products").document(productName).get().addOnSuccessListener(documentSnapshot -> {
            IProduct product = documentSnapshot.toObject(Product.class);
            fireStoreCallback.Callback(product);
        });
    }

    //get the all products of specific category according to its title directly from database
    public void getAllProductsByCategoryTitle(FireStoreCallback fireStoreCallback, String categoryTitle) {
        List<IProduct> products = new ArrayList<>();
        db.collection("Products")
                .whereEqualTo("categoryTitle", categoryTitle)
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

    public <T> void updateIncrement(String productName, String fieldName, T step) {
        DocumentReference products = db.collection("Products").document(productName);
        products.update(fieldName, FieldValue.increment((int) step));
    }

}
