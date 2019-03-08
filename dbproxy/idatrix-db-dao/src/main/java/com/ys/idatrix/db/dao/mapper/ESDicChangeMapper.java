package com.ys.idatrix.db.dao.mapper;

import com.ys.idatrix.db.annotation.TargetDataSource;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName: ESDicChangeMapper
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/9/19
 */
@TargetDataSource(name = "datalab")
public interface ESDicChangeMapper {

    Integer existTable(@Param("tenantId") String tenantId);
}
