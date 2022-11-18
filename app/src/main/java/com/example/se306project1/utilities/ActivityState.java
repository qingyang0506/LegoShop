package com.example.se306project1.utilities;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @Description: This is ActivityState class which is used for every Activity class
 * @author: Frank Ji
 * @date: 14/08/2022
 */
public class ActivityState {

    private static final ActivityState activityState = new ActivityState();

    private AppCompatActivity currentActivity;

    private ActivityState() {
    }

    public static ActivityState getInstance() {
        return activityState;
    }

    public AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

}
