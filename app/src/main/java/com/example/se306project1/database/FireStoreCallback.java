package com.example.se306project1.database;

/**
 * @Description: This is FireStoreCallback interface, which is used for callback
 * @author: Qingyang Li
 * @date: 17/08/2022
 */
public interface FireStoreCallback {
    <T> void Callback(T value);
}
