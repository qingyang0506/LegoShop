package com.example.se306project1.database;

/**
 * @Description: This is IUserDatabase class which used for some operations for userDatabase
 * @author: XiaoXiao Zhuang
 * @date: 16/08/2022
 */
public interface IUserDatabase {
    void isUserExist(FireStoreCallback fireStoreCallback, String username);

    void isLoginValid(FireStoreCallback fireStoreCallback, String username, String password);

    void addUserToFireStore(String username, String password);
}
