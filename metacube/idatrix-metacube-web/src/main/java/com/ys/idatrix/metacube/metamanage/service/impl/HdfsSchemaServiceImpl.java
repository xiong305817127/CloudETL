package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import com.ys.idatrix.metacube.common.enums.SchemaOperationTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.common.utils.ValidateUtil;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service("hdfsSchemaService")
public class HdfsSchemaServiceImpl implements McSchemaService {

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private HdfsUnrestrictedService hdfsService;

    /**
     * 为McSchemaService接口注入McSchemaMapper数据访问接口
     */
    @Override
    public McSchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    /**
     * 新建模式 新建需要在平台库上创建目录
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public McSchemaPO create(McSchemaPO schemaPO) {
        ValidateUtil.checkHdfsPath(schemaPO.getName());
        checkDirectoryExists(schemaPO.getName(), UserUtils.getRenterId());
        insert(schemaPO);
        RespResult<Boolean> result =
                hdfsService.createDir(UserUtils.getUserName(), schemaPO.getName());
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return schemaPO;
    }

    /**
     * 注册模式 只在模式表新增记录
     */
    @Override
    public McSchemaPO register(McSchemaPO schemaPO) {
        return insert(schemaPO);
    }

    /**
     * 检查是否已存在目录或父目录
     *
     * @return 已存在返回true，不存在返回false
     */
    public boolean checkDirectoryExists(String path, Long renterId) {
        List<String> pathList = returnAllSubSequence(path);
        List<McSchemaPO> list = schemaMapper.listDirectory(pathList, renterId);
        return !CollectionUtils.isEmpty(list);
    }

    private List<String> returnAllSubSequence(String path) {
        String[] array = path.substring(1).split("/");

        List<String> list = new ArrayList<>(array.length);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append("/").append(array[i]);
            list.add(builder.toString());
        }
        System.out.println(list);
        return list;
    }

    /**
     * 删除模式
     */
    @Override
    public McSchemaPO delete(McSchemaPO schemaPO) {
        if (schemaPO.getType() == SchemaOperationTypeEnum.REGISTER.getCode()) {
            return schemaPO;
        }
        RespResult<Boolean> result =
                hdfsService.deleteDir(Arrays.asList(schemaPO.getName()), true);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return schemaPO;
    }
}
