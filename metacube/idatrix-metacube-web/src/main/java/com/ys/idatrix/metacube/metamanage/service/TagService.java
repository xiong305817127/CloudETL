package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.Tag;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TagService
 * @Description tag 服务层api
 * @Author ouyang
 * @Date
 */
public interface TagService {

    // 查询用户下使用过的所有标签
    List<Tag> findTagList();

    // 查询租户下使用过的所有标签名字
    List<String> findTagListByRenterId(Long renterId);

    int insertTags(String tags, Long renterId, String creator, Date createTime);
}
