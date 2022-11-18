package com.example.se306project1.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se306project1.R;
import com.example.se306project1.activities.DetailActivity;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.utilities.ActivityState;

import java.util.List;

/**
 * @Description: This is TopPickAdapter class which used for top likes product in categoryActivity
 * @author: Frank Ji
 * @date: 16/08/2022
 */
public class TopPickAdapter extends RecyclerView.Adapter<TopPickAdapter.TopPickViewHolder> {

    public static class TopPickViewHolder extends RecyclerView.ViewHolder {
        private final ImageView topPickImageView;
        private final TextView topPickNameTextView;
        private final CardView container;

        public TopPickViewHolder(final View view) {
            super(view);
            this.topPickImageView = view.findViewById(R.id.top_pick_imageview);
            this.topPickNameTextView = view.findViewById(R.id.top_pick_text_view);
            this.container = view.findViewById(R.id.top_pick_container);
        }
    }

    private final List<IProduct> products;

    public TopPickAdapter(List<IProduct> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public TopPickAdapter.TopPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPickLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_pick_layout_view, parent, false);
        return new TopPickViewHolder(topPickLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPickViewHolder holder, int position) {
        IProduct product = this.products.get(position);
        holder.topPickImageView.setImageResource(product.getImages().get(0));
        holder.topPickNameTextView.setText(product.getName());
        holder.container.setOnClickListener(view -> DetailActivity.startWithName(product.getName()));
    }

    @Override
    public int getItemCount() {
        return this.products.size();
    }
}
