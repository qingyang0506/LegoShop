package com.example.se306project1.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se306project1.R;
import com.example.se306project1.activities.DetailActivity;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.models.Product;
import com.example.se306project1.utilities.ActivityState;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: This is SuggestAdapter class which used for the suggest list in ProductSearcher
 * @author: Frank Ji
 * @date: 15/08/2022
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>
        implements Filterable {

    //this three list container is used for the filtering the target products
    private final List<IProduct> allProducts;
    private List<IProduct> filteredProducts = new ArrayList<>();
    private final List<IProduct> emptyProductList = new ArrayList<>();

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView suggestedNameTextView;
        private final CardView suggestionItemContainer;

        public SuggestionViewHolder(View view) {
            super(view);
            this.suggestedNameTextView = view.findViewById(R.id.suggested_product_name_textview);
            this.suggestionItemContainer = view.findViewById(R.id.suggestion_item_container);
        }
    }

    public SuggestionAdapter(List<IProduct> products) {
        this.allProducts = products;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_item, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        holder.suggestedNameTextView.setText(this.filteredProducts.get(position).getName());
        holder.suggestionItemContainer.setOnClickListener(view -> {
            if (holder.suggestedNameTextView.getText().toString().equalsIgnoreCase("no result"))
                return;
            DetailActivity.startWithName(holder.suggestedNameTextView.getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return this.filteredProducts.size();
    }

    /**
     * @return Filter
     * @Description: this method will filter the product which name is contains the input string instantly
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString();
                if (searchText.isEmpty()) {
                    filteredProducts = emptyProductList;
                } else {
                    List<IProduct> temp = new ArrayList<>();
                    for (IProduct product : allProducts) {
                        if (product.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            temp.add(product);
                        }
                    }
                    filteredProducts = temp;
                }
                if (!searchText.isEmpty() && filteredProducts.isEmpty()) {
                    IProduct product = new Product();
                    product.setName("No Result");
                    filteredProducts.add(product);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredProducts;
                return filterResults;
            }

            //when the input changed, we will notify this and do a filter again.
            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredProducts = (ArrayList<IProduct>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
