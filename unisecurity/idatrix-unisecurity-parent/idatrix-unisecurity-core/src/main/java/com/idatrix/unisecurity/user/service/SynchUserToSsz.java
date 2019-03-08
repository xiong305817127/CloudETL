package com.idatrix.unisecurity.user.service;

/**
 * @ClassName SynchUserToSsz
 * @Description 同步用户到神算子中
 * @Author ouyang
 * @Date 2018/11/7 10:22
 * @Version 1.0
 */
public interface SynchUserToSsz {

    // 一键导入所有用户到神算子中
    void importAll();

    // 新增用户同步到神算子中
    void addUser(String userName);

    // 删除用户同步到神算子中
    void deleteUser(String userNames);

}
