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
import com.example.se306project1.activities.ProductActivity;
import com.example.se306project1.models.ICategory;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.AnimationFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * @Description: This is CategoryAdapter class which used for category in CategoryActivity
 * @author: XiaoXiao Zhuang
 * @date: 11/08/2022
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitleTextview;
        private final FloatingActionButton forwardButton;
        private final ImageView imageView;
        private final CardView cardView;
        private final View view;

        public CategoryViewHolder(final View view) {
            super(view);
            this.categoryTitleTextview = view.findViewById(R.id.category_title_textview);
            this.forwardButton = view.findViewById(R.id.forward_arrow_button);
            this.imageView = view.findViewById(R.id.category_imageview);
            this.cardView = view.findViewById(R.id.category_card_view);
            this.view = view;
        }
    }

    /**
     * @Description: this list is used for recyclerView
     */
    private final List<ICategory> categories;

    public CategoryAdapter(List<ICategory> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout_view, parent, false);
        return new CategoryViewHolder(categoryLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String CategoryTitle = this.categories.get(position).getTitle();
        holder.categoryTitleTextview.setText(CategoryTitle);
        holder.forwardButton.setOnClickListener(view -> ProductActivity.startWithTheme(
                this.categories.get(position).getTitle()
        ));
        holder.cardView.setOnClickListener(view -> ProductActivity.startWithTheme(
                this.categories.get(position).getTitle()
        ));
        holder.imageView.setImageResource(this.categories.get(position).getImage());
        holder.view.startAnimation(
                new AnimationFactory().getSlideFromLeftAnimation()
        );
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }

}
