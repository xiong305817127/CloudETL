package com.ys.idatrix.db.init;

import com.google.common.collect.Maps;
import com.ys.idatrix.db.api.rdb.dto.RdbEnum;
import com.ys.idatrix.db.service.internal.IRdbDDL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: RdbDDLServiceAware
 * <p>
 * DBDDL的工场类：初始化所有的IRdbDDL实现
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/8/6
 */
@Slf4j
@Component
public class RdbDDLServiceAware implements ApplicationContextAware {

    private static Map<RdbEnum.DBType, IRdbDDL> rdbDDLMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IRdbDDL> map = applicationContext.getBeansOfType(IRdbDDL.class);
        if (MapUtils.isNotEmpty(map)) {
            log.info("加载IRdbDDL 实现 " + map.size() + "个");
        }
        rdbDDLMap = Maps.newHashMap();
        map.forEach((key, value) -> rdbDDLMap.put(value.getDBType(), value));
    }

    /**
     * 根据 DBType 获取实际的DDL处理类
     *
     * @param code
     * @param <T>
     * @return
     */
    public static <T extends IRdbDDL> T getRealRdbDDL(RdbEnum.DBType code) {
        return (T) rdbDDLMap.get(code);
    }

    /**
     * 根据 typeName 获取实际的DDL处理类
     *
     * @param typeName
     * @param <T>
     * @return
     */
    public static <T extends IRdbDDL> T getRealRdbDDL(String typeName) {
        return (T) rdbDDLMap.get(RdbEnum.DBType.valueOf(typeName));
    }
}
