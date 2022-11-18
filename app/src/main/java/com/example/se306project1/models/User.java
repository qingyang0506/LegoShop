package com.example.se306project1.models;

/**
 * @Description: This is User class which is implement the IUser interface
 * @author: Qingyang Li
 * @date: 9/08/2022
 */
public class User implements IUser {

    private String username;
    private String password;

    // No-arg constructor is not used anywhere, but it is necessary for Firestore deserialization
    @SuppressWarnings("unused")
    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
