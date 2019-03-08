package com.idatrix.unisecurity.user.service;

/**
 * @ClassName SynchUserToBbs
 * @Description 同步用户到bbs中
 * @Author ouyang
 * @Date 2018/11/7 10:11
 * @Version 1.0
 */
public interface SynchUserToBbs {

    // 新增用户同步到bbs中
    void addUser(String username, String password, String email, String mobile);

    // 修改用户同步到bbs中
    void updateUser(String username, String password, String phone, String email, Boolean encrypted);

    // 删除用户同步到bbs中
    void deleteUser(String userNames);
}
