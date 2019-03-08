package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.enums.SchemaOperationTypeEnum;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaAddVO;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaUpdateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/schemas")
@Api(value = "api/v1/schemas", tags = "元数据管理-服务器&数据库-模式处理接口")
public class McSchemaController {

    @Autowired
    @Qualifier("schemaServiceImpl")
    private McSchemaService schemaService;

    /**
     * 新建模式
     */
    @PostMapping
    @ApiOperation("新建/注册数据库模式")
    public ResultBean<McSchemaPO> create(@Valid @RequestBody SchemaAddVO schemaAddVO) {
        McSchemaPO schemaPO = new McSchemaPO();
        BeanUtils.copyProperties(schemaAddVO, schemaPO);
        schemaPO.setRenterId(UserUtils.getRenterId());
        schemaPO.fillCreateInfo(schemaPO, UserUtils.getUserName());
        if (schemaPO.getType() == SchemaOperationTypeEnum.CREATE.getCode()) {
            return ResultBean.ok(schemaService.create(schemaPO));
        }
        if (schemaPO.getType() == SchemaOperationTypeEnum.REGISTER.getCode()) {
            return ResultBean.ok(schemaService.register(schemaPO));
        }
        return ResultBean.ok();
    }

    /**
     * 模式列表
     */
    @GetMapping
    @ApiOperation("模式列表")
    public ResultBean<PageResultBean<List<McSchemaPO>>> listSchemas(SchemaSearchVO searchVO) {
        return ResultBean.ok(schemaService.listByPage(searchVO));
    }

    /**
     * 模式详情
     */
    @GetMapping("/{id}")
    @ApiOperation("模式详情")
    public ResultBean<McSchemaPO> getSchemaById(@PathVariable("id") Long id) {
        return ResultBean.ok(schemaService.getSchemaById(id));
    }

    /**
     * 更新模式 模式名称不能修改
     */
    @PutMapping("/{id}")
    @ApiOperation("编辑模式")
    public ResultBean<McSchemaPO> update(@PathVariable("id") Long id,
            @RequestBody SchemaUpdateVO schemaUpdateVO) {
        McSchemaPO schemaPO = new McSchemaPO();
        schemaPO.setId(id);
        BeanUtils.copyProperties(schemaUpdateVO, schemaPO);

        // TODO 需回写安全的所属组织使用计数器
        return ResultBean.ok(schemaService.update(schemaPO));
    }

    /**
     * 删除模式 逻辑删除
     *
     * @param id 模式id
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation("删除模式")
    public ResultBean<McSchemaPO> delete(@PathVariable("id") Long id) {
        // TODO 需回写安全的所属组织使用计数器
        return ResultBean.ok(schemaService.delete(schemaService.getSchemaById(id)));
    }
}

