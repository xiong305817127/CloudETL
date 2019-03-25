package com.idatrix.unisecurity.user.service;

/**
 * @ClassName SyncUserToBeijing
 * @Description
 * @Author ouyang
 * @Date
 */
public interface SyncUserToBeijing {

    void addUser(String username, String password, String realName, String email);

    void updateUser(String username, String password, String realName, String email);

    void delete(String userNames);

    void importAll();
}