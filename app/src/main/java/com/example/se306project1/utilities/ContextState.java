package com.example.se306project1.utilities;

import android.content.Context;

/**
 * @Description: This is ContextState class which is used for context
 * @author: Frank Ji
 * @date: 17/08/2022
 */
public class ContextState {

    private static final ContextState contextState = new ContextState();

    private Context currentContext;

    private ContextState() {
    }

    public static ContextState getInstance() {
        return contextState;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentActivity) {
        this.currentContext = currentActivity;
    }

}
