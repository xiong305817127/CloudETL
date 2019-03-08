package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Tag;
import com.ys.idatrix.metacube.metamanage.mapper.TagMapper;
import com.ys.idatrix.metacube.metamanage.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TagServiceImpl
 * @Description tag 服务层实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Transactional
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> findTagList() {
        List<Tag> list = tagMapper.findTagListByUserId(UserUtils.getUserId());
        return list;
    }

    @Override
    public List<String> findTagListByRenterId(Long renterId) {
        List<String> list = tagMapper.findTagListByRenterId(renterId);
        return list;
    }

    @Override
    public int insertTags(String tags, Long renterId, String creator, Date createTime) {
        int count = 0;
        if (StringUtils.isNotBlank(tags)) {
            String[] tagArr = tags.split(",");
            for (String tagStr : tagArr) {
                Tag tag = new Tag();
                tag.setRenterId(renterId);
                tag.setCreator(creator);
                tag.setCreateTime(createTime);
                tag.setIsDeleted(false);
                tag.setTagName(tagStr);
                if (tagMapper.findByTag(tag) <= 0) {
                    // 当前用户下此标签不存在
                    count += tagMapper.insertSelective(tag);
                }
            }
        }
        return count;
    }
}
