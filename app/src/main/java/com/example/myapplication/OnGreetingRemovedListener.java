package com.example.myapplication;

/**
 * The listener used to handle a greeting removal.
 */
public interface OnGreetingRemovedListener {
    /**
     * Called when a greeting is removed.
     * @param greeting the greeting
     */
    void onGreetingRemoved(String greeting);
}