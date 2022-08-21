package com.example.se306project1.utilities;

import com.example.se306project1.database.CartDatabase;
import com.example.se306project1.database.ProductDatabase;
import com.example.se306project1.models.CartProduct;
import com.example.se306project1.models.IProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartState {

    private static CartState cartState = new CartState();

    private List<CartProduct> cartProducts;
    private List<String> checkedProducts;

    private CartState() {
        this.cartProducts = new ArrayList<>();
        this.checkedProducts = new ArrayList<>();
    }

    public static CartState getCartState() {
        return cartState;
    }

    public void setCartList(List<CartProduct> cartProducts) {
        this.cartProducts.clear();
        this.cartProducts.addAll(cartProducts);
    }

    public List<CartProduct> getCartProducts() {
        return this.cartProducts;
    }

    public void addToCart(CartProduct cartProduct) {
        for (CartProduct c: cartProducts) {
            if (c.getName().equals(cartProduct.getName())) return;
        }
        this.cartProducts.add(cartProduct);
        CartDatabase db = CartDatabase.getInstance();
        db.addProductToCart(UserState.getInstance().getCurrentUser().getUsername(),cartProduct.getName());
    }

    public void updateAmount(String productName, int newAmount) {
        for (CartProduct c: this.cartProducts) {
            if (c.getName().equals(productName)) {
                c.setAmount(newAmount);
            }
        }
    }

    public void removeCartProduct(String productName) {
        for (CartProduct c: this.cartProducts) {
            if (c.getName().equals(productName)) {
                this.cartProducts.remove(c);
                break;
            }
        }
        CartDatabase db = CartDatabase.getInstance();
        db.removeProductFromCart(UserState.getInstance().getCurrentUser().getUsername(),productName);
    }

    public void checkItem(String productName) {
        if (this.checkedProducts.contains(productName)) {
            return;
        };
        for (int i = 0; i < this.cartProducts.size(); i++) {
            if (this.cartProducts.get(i).getName().equals(productName)) {
                this.checkedProducts.add(this.cartProducts.get(i).getName());
            }
        }
    }

    public void uncheckItem(String productName) {
        this.checkedProducts.remove(productName);
    }

    public double getPrice() {
        double total = 0;
        for (String productName: this.checkedProducts) {
            CartProduct cartProduct = this.cartProducts.stream().filter(c -> c.getName().equals(productName)).findFirst().get();
            total += cartProduct.getPrice() * cartProduct.getAmount();
        }
        return total;
    }

    public void checkAll() {
        this.checkedProducts.clear();
        for (CartProduct c: this.cartProducts) {
            this.checkedProducts.add(c.getName());
        }
    }

    public void uncheckAll() {
        this.checkedProducts.clear();
    }

    public boolean isAllChecked() {
        return this.checkedProducts.size() == this.cartProducts.size();
    }

    public boolean isItemChecked(String productName) {
        return this.checkedProducts.contains(productName);
    }

    public void checkout() {
        List<String> productToCheckout = new ArrayList<>();
        for (String productName: this.checkedProducts) {
            CartProduct cartProduct = this.cartProducts
                    .stream()
                    .filter(c -> c.getName().equals(productName))
                    .findFirst()
                    .orElseGet(null);
            if (cartProduct == null) continue;
            productToCheckout.add(productName);
            ProductDatabase.getInstance().updateProductInfo(
                    productName,
                    "stock",
                    cartProduct.getStock() - cartProduct.getAmount()
            );
            CartDatabase.getInstance().removeProductFromCart(
                    UserState.getInstance().getCurrentUser().getUsername(),
                    productName
            );
        }
        for (String product: productToCheckout) {
            this.uncheckItem(product);
        }
    }

    public void userLogout() {
        this.checkedProducts.clear();
        this.cartProducts.clear();
    }

}
