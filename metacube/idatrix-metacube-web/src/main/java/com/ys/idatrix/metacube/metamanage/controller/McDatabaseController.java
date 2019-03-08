package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.RdbService;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.vo.request.DatabaseAddOrUpdateVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TestDatabaseConnectionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/databases")
@Api(value = "api/v1/databases", tags = "元数据管理-服务器&数据库-数据库")
public class McDatabaseController {

    private static final Map<Integer, DatabaseTypeEnum> DATABASE_TYPE_MAP = new HashMap<>();

    static {
        for (DatabaseTypeEnum typeEnum : DatabaseTypeEnum.values()) {
            DATABASE_TYPE_MAP.put(typeEnum.getCode(), typeEnum);
        }
    }

    @Autowired
    private McDatabaseService databaseService;

    @Autowired
    private RdbService rdbService;


    @GetMapping(value = "exists")
    @ApiOperation(value = "检查数据库是否已存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "服务器ip", required = true),
            @ApiImplicitParam(name = "type", value = "数据库类型 1 MySQL 2 Oracle 3 DM 4 PostgreSQL 5 Hive 6 HBase 7 HDFS 8 Elasticsearch", required = true),
    })
    public ResultBean<Boolean> existsByIpAndType(String ip, Integer type) {
        checkDatabaseType(type);
        return ResultBean.ok(databaseService.exists(ip, type, UserUtils.getRenterId()));
    }

    @PostMapping
    @ApiOperation(value = "新增数据库")
    public ResultBean<McDatabasePO> insert(
            @Validated @RequestBody DatabaseAddOrUpdateVO databaseAddVO) {
        // mysql需要单独校验管理员账号和密码
        if (databaseAddVO.getType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            validUsernameAndPassword(databaseAddVO.getUsername(), databaseAddVO.getPassword());
        }
        McDatabasePO databasePO = new McDatabasePO();
        BeanUtils.copyProperties(databaseAddVO, databasePO);
        databasePO.fillCreateInfo(databasePO, UserUtils.getUserName());
        return ResultBean.ok(databaseService.register(databasePO));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改数据库")
    public ResultBean<McDatabasePO> update(@PathVariable("id") Long id,
            @RequestBody DatabaseAddOrUpdateVO serverUpdateVO) {
        McDatabasePO databasePO = new McDatabasePO();
        databasePO.setId(id);
        BeanUtils.copyProperties(serverUpdateVO, databasePO);
        return ResultBean.ok(databaseService.update(databasePO));
    }

    @GetMapping("/{id}")
    @ApiOperation("数据库详情")
    public ResultBean<McDatabasePO> getDatabaseById(@PathVariable("id") Long id) {
        return ResultBean.ok(databaseService.getDatabaseById(id));
    }

    @ApiOperation(value = "注销数据库")
    @DeleteMapping("/{id}")
    public ResultBean delete(@PathVariable Long id) {
        databaseService.delete(id, UserUtils.getUserName());
        return ResultBean.ok("注销成功");
    }

    /**
     * 校验管理员账号和密码
     */
    private void validUsernameAndPassword(String username, String password) {
        if (StringUtils.isBlank(username)) {
            throw new MetaDataException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "管理员账号不能为空");
        }
        if (StringUtils.isBlank(password)) {
            throw new MetaDataException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "管理员密码不能为空");
        }
    }

    /**
     * 测试数据库连接
     */
    @ApiOperation(value = "连接测试")
    @GetMapping("connection/test")
    public ResultBean<Boolean> testDBLink(TestDatabaseConnectionVO connectionVO) {
        RdbLinkDto dto = new RdbLinkDto(connectionVO.getUsername(),
                connectionVO.getPassword(),
                "MYSQL",
                connectionVO.getIp(),
                connectionVO.getPort(),
                connectionVO.getDbName());

        RespResult<Boolean> result = rdbService.testDBLink(dto);
        return result.isSuccess() ? ResultBean.ok(Boolean.TRUE) :
                ResultBean.ok(result.getMsg(), Boolean.FALSE);
    }

    /**
     * 检查数据库类型
     */
    private void checkDatabaseType(int type) {
        boolean result = DATABASE_TYPE_MAP.containsKey(type);
        if (result == false) {
            throw new MetaDataException("未知的数据库类型: " + type);
        }
    }
}

