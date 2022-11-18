package com.example.se306project1.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se306project1.R;
import com.example.se306project1.activities.DetailActivity;
import com.example.se306project1.database.CartDatabase;
import com.example.se306project1.models.CartProduct;
import com.example.se306project1.utilities.CartState;
import com.example.se306project1.utilities.StringBuilder;
import com.example.se306project1.utilities.UserState;

import java.util.List;
import java.util.Locale;

/**
 * @Description: This is CartProductAdapter class which used for cart product in cartActivity
 * @author: Frank ji
 * @date: 12/08/2022
 */
public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder> {

    public static class CartProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, amountTextView, priceTextView;
        private final ImageView imageView;
        private final Button decreaseAmountButton, increaseAmountButton, deleteButton;
        private final CheckBox checkBox;
        private final CardView cartContainer;

        /**
         * @Description: retrieve the used UI element by id
         * @author: Frank ji
         * @date: 12/08/2022
         */
        public CartProductViewHolder(final View view) {
            super(view);
            this.nameTextView = view.findViewById(R.id.cart_product_name_textview);
            this.amountTextView = view.findViewById(R.id.cart_product_amount_textview);
            this.priceTextView = view.findViewById(R.id.cart_product_price_textview);
            this.imageView = view.findViewById(R.id.cart_product_imageview);
            this.decreaseAmountButton = view.findViewById(R.id.decrease_amount_button);
            this.increaseAmountButton = view.findViewById(R.id.increase_amount_button);
            this.deleteButton = view.findViewById(R.id.delete_button);
            this.checkBox = view.findViewById(R.id.cart_product_checkbox);
            this.cartContainer = view.findViewById(R.id.cart_item_container);
        }
    }

    private final List<CartProduct> products;
    private final TextView totalPriceTextview;
    private final CheckBox selectAllCheckBox;

    public CartProductAdapter(List<CartProduct> products, TextView totalPriceTextview, CheckBox checkBox) {
        this.products = products;
        this.totalPriceTextview = totalPriceTextview;
        this.selectAllCheckBox = checkBox;
    }

    @NonNull
    @Override
    public CartProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cartProductListItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_product_list_item, parent, false);
        return new CartProductViewHolder(cartProductListItem);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CartProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartProduct cartProduct = this.products.get(position);
        holder.nameTextView.setText(cartProduct.getName());
        holder.priceTextView.setText(
                new StringBuilder(R.string.price_tag)
                        .set("price", String.format(Locale.ENGLISH, "%.2f", cartProduct.getPrice()))
                        .toString()
        );
        holder.amountTextView.setText(
                new StringBuilder(R.string.cart_product_amount)
                        .set("amount", cartProduct.getAmount())
                        .toString()
        );
        holder.decreaseAmountButton.setOnClickListener(view -> updateAmount(holder.amountTextView, position, products.get(position).getAmount() - 1));
        holder.increaseAmountButton.setOnClickListener(view -> updateAmount(holder.amountTextView, position, products.get(position).getAmount() + 1));
        holder.deleteButton.setOnClickListener(view -> {
            deleteProduct(position);
            notifyDataSetChanged();
        });
        holder.imageView.setImageResource(cartProduct.getImages().get(0));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                CartState.getCartState().checkItem(cartProduct.getName());
            } else {
                CartState.getCartState().uncheckItem(cartProduct.getName());
            }
            selectAllCheckBox.setChecked(CartState.getCartState().isAllChecked());
            updatePrice();
        });
        holder.checkBox.setChecked(CartState.getCartState().isItemChecked(cartProduct.getName()));
        holder.cartContainer.setOnClickListener(v -> DetailActivity.startWithName(cartProduct.getName()));
    }

    @Override
    public int getItemCount() {
        return this.products.size();
    }

    /**
     * @Description: update the total price in cart activity
     */
    private void updatePrice() {
        String priceTag = new StringBuilder(R.string.price_tag)
                .set("price", CartState.getCartState().getPriceString())
                .toString();
        totalPriceTextview.setText(priceTag);
    }

    /**
     * @param amountTextView TextView
     * @param position       int
     * @param amount         int
     * @Description: manage the amount of the product which is in your shopping cart. And update the data in database
     */
    private void updateAmount(TextView amountTextView, int position, int amount) {
        CartProduct cartProduct = products.get(position);
        //the amount of product you want to buy must at least 1 and no more than stock number
        amount = Math.max(amount, 1);
        amount = Math.min(amount, cartProduct.getStock());
        String amountString = new StringBuilder(R.string.cart_product_amount)
                .set("amount", amount)
                .toString();
        amountTextView.setText(amountString);
        cartProduct.setAmount(amount);
        CartState.getCartState().updateAmount(cartProduct.getName(), amount);
        CartDatabase db = CartDatabase.getInstance();
        db.subtractCartAmount(UserState.getInstance().getCurrentUser().getUsername(), cartProduct.getName());
        updatePrice();
    }

    /**
     * @param position int
     * @Description: delete product from your shopping cart for the delete button in product card
     */
    private void deleteProduct(int position) {
        CartProduct cartProduct = products.get(position);
        products.remove(position);
        CartState.getCartState().uncheckItem(cartProduct.getName());
        CartState.getCartState().removeCartProduct(cartProduct.getName());
        updatePrice();
    }
}
