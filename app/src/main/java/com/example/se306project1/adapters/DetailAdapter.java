package com.example.se306project1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.se306project1.R;

import java.util.List;

/**
 * @Description: This is DetailAdapter class which used for viewPager in DetailActivity
 * @author: Qingyang Li
 * @date: 11/08/2022
 */
public class DetailAdapter extends PagerAdapter {

    //this list of image is used for the viewPager recyclerView
    List<Integer> imageList;

    public DetailAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.image_slide, container, false);
        ImageView image = view.findViewById(R.id.imageSlide);
        image.setImageResource(imageList.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
