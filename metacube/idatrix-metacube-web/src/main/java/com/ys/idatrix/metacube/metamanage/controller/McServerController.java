package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McServerDatabaseChangePO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.vo.request.ChangeSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ServerAddOrUpdateVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ServerSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.response.ServerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("api/v1/servers")
@Api(value = "api/v1/servers", tags = "元数据管理-服务器&数据库-服务器")
public class McServerController {

    @Autowired
    private McServerService serverService;

    @GetMapping
    @ApiOperation(value = "服务器列表")
    public ResultBean<PageResultBean<List<ServerVO>>> list(ServerSearchVO searchVO) {
        searchVO.setRenterId(UserUtils.getRenterId());
        return ResultBean.ok(serverService.list(searchVO));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "服务器详情")
    public ResultBean<McServerPO> getServerById(@PathVariable Long id) {
        return ResultBean.ok(serverService.getServerById(id));
    }

    @PostMapping
    @ApiOperation(value = "注册服务器")
    public ResultBean<McServerPO> insert(
            @Validated @RequestBody ServerAddOrUpdateVO serverAddVO) {
        McServerPO serverPO = new McServerPO();
        BeanUtils.copyProperties(serverAddVO, serverPO);
        return ResultBean.ok(serverService.register(serverPO));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改服务器")
    public ResultBean<McServerPO> update(@PathVariable Long id,
            @Validated @RequestBody ServerAddOrUpdateVO serverUpdateVO) {
        McServerPO serverPO = new McServerPO();
        serverPO.setId(id);
        BeanUtils.copyProperties(serverUpdateVO, serverPO);
        return ResultBean.ok(serverService.update(serverPO));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "注销服务器")
    public ResultBean delete(@PathVariable Long id) {
        serverService.delete(id, UserUtils.getUserName());
        return ResultBean.ok("注销成功");
    }

    @ApiOperation(value = "服务器的变更记录")
    @GetMapping("/{id}/changelog")
    @ApiImplicitParam(name = "id", value = "服务器id", required = true)
    public ResultBean<PageResultBean<List<McServerDatabaseChangePO>>> search(
            @PathVariable Long id, ChangeSearchVO searchVO) {
        searchVO.setType(1);
        searchVO.setFkId(id);
        return ResultBean.ok(serverService.listChangeLog(searchVO));
    }
}

