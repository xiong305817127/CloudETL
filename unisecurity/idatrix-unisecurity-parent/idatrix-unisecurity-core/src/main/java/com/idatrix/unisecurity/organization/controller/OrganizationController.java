package com.idatrix.unisecurity.organization.controller;

import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.domain.Organization;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.*;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.organization.bo.OrganizationBo;
import com.idatrix.unisecurity.organization.service.OrganizationService;
import com.idatrix.unisecurity.organization.vo.OrganizationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/organization")
@Api(value = "/OrganizationController", tags = "安全管理-组织管理处理接口")
public class OrganizationController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganizationService organizationService;

    @ApiOperation(value = "查询用户所属租户下的组织列表", notes = "可以通过findContent进行关键字查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "当前显示第几页", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "当前显示几条数据", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "findContent", value = "查询输入条件", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultVo list(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                         @RequestParam(required = false) String findContent, ModelMap modelMap) {
        modelMap.put("findContent", findContent);
        modelMap.put("renterId", ShiroTokenManager.getToken().getRenterId());
        Pagination<Organization> organizations = organizationService.findPage(modelMap, pageNo, pageSize);
        return ResultVoUtils.ok(organizations);
    }

    @ApiOperation(value = "新增组织", notes = "")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultVo add(Organization organization) throws Exception {
        UUser user = ShiroTokenManager.getToken();
        if (user == null && user.getRenterId() == null) {
            return ResultVoUtils.error(ResultEnum.PARAM_ERROR.getCode(), "登录用户所属租户未指定，无权新增组织");
        }
        organization.setRenterId(user.getRenterId());
        organization.setCreateTime(new Date());
        organization.setLastUpdatedBy(new Date());
        organization.setIsActive(true);

        // 校验参数
        checkParam(organization, true);

        // insert
        organizationService.insert(organization);
        return ResultVoUtils.ok("新增部门成功");
    }

    @ApiOperation(value = "修改组织", notes = "")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultVo updateDepartment(Organization organization) {
        checkParam(organization, false);
        organizationService.updateOrganization(organization);
        return ResultVoUtils.ok("修改成功！！！");
    }

    private void checkParam(Organization organization, Boolean isAdd) {
        String msg = "新增失败，";
        if (!isAdd) {
            msg = "修改失败，";
        }
        // 组织机构代码不能重复
        if (organizationService.findDeptCountByCode(organization) > 0) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), msg + "组织机构代码重复");
        }

        // 统一社会信用代码不能重复
        if (organizationService.findByUnifiedCreditCode(organization) > 0) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), msg + "统一社会信用代码重复");
        }

        // 同一层级的组织名称应该不允许重名
        if (organizationService.findOrganizationByParentIdName(organization) > 0) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), msg + "同一层级的组织机构名称不可重名");
        }
    }

    @ApiOperation(value = "根据组织ids删除组织", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "组织ids，以,隔开", required = true, dataType = "String", paramType = "form"),
    })
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultVo deleteDepartments(String ids) throws Exception {
        return ResultVoUtils.ok(organizationService.deleteOrganizationById(ids));
    }

    @ApiOperation(value = "查询用户组织", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "int", paramType = "query"),
    })
    @RequestMapping(value = "/rent-organization", method = RequestMethod.GET)
    public ResultVo getOrganizationByUser(@NotNull(message = "userId不能为空") Long userId) {
        List<Organization> organizations = organizationService.selectOrganizationByUserId(userId);
        return ResultVoUtils.ok(organizations);
    }

    @ApiOperation(value = "导出选中的组织", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "组织id，以,隔开", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportOrganization(@NotBlank(message = "请选择组织后再进行导出！！！") String ids, HttpServletResponse response) throws IOException {
        if (ids.split(",").length <= 0) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(GsonUtil.toJson(ResultVoUtils.error(500, "请选择组织后再进行导出！！！")));
            return;
        }
        List<OrganizationBo> organizations = organizationService.export(ids);
        String[] titles = {"上级组织机构代码", "组织机构编码", "组织机构名称", "上级组织机构名称", "统一社会信用代码"};
        WriteExcel<OrganizationBo> writeExcel = new WriteExcel<>();
        writeExcel.createExcel("组织机构信息表", titles, organizations, response);
    }

    @ApiOperation(value = "导入组织", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "excel文件", required = true, dataType = "file", paramType = "form")
    })
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResultVo importOrganization(@NotNull(message = "文件不能为空") @RequestParam("file") MultipartFile file,
                                       HttpServletRequest request) throws Exception {
        try {
            String fileName = file.getOriginalFilename();//文件名
            String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);//后缀名
            if (!"xls".equals(fileExtName) && !"xlsx".equals(fileExtName)) {
                return ResultVoUtils.error(ResultEnum.PARAM_ERROR.getCode(), "不支持的文件类型");
            }

            String path = request.getServletContext().getRealPath("/upload/excel");
            File newfile = new File(path + "/" + fileName);
            FileOutputStream fos = FileUtils.openOutputStream(newfile);
            IOUtils.copy(file.getInputStream(), fos);
            file.getInputStream().close();
            fos.flush();
            fos.close();
            Organization organization = null;
            logger.debug("readExcel begin:" + newfile.getPath());
            List<List<Object>> dataSet = ReadExcel.readExcel(newfile);
            logger.debug("readExcel end result: " + JSON.toJSONString(dataSet));
            int count = 1;
            int successCount = 0;
            List<String> errorList = new ArrayList<>(); // 错误信息

            if (CollectionUtils.isEmpty(dataSet)) {
                return ResultVoUtils.error(ResultEnum.PARAM_ERROR.getCode(), "表格无数据");
            }

            UUser user = ShiroTokenManager.getToken();
            for (List<Object> cells : dataSet) {
                logger.debug("load  organization : " + JSON.toJSONString(cells));
                organization = new Organization();
                organization.setRenterId(user.getRenterId());
                // get parent id
                if (!StringUtils.isEmpty((String) cells.get(0))) {
                    Organization param = new Organization();
                    param.setDeptCode((String) cells.get(0));
                    param.setRenterId(user.getRenterId());
                    logger.debug("第" + count + "行  findDeptByCode params: " + JSON.toJSONString(param));
                    List<Organization> result = organizationService.findDeptByCode(param);
                    if (result == null || result.size() == 0) {
                        errorList.add("第" + count++ + "行错误:" + (String) cells.get(2) + "上级部门不存在，请先检查");
                        continue;
                    }
                    organization.setParentId(result.get(0).getId());
                } else { // 如果上级部门为空，设置 租户部门id 为ParentId
                    organization.setParentId(user.getDeptId());
                }

                organization.setDeptCode((String) cells.get(1));
                organization.setDeptName((String) cells.get(2));

                if (cells.size() >= 4 && cells.get(3) != null && !StringUtils.isEmpty((String) cells.get(3))) {
                    if (cells.size() >= 5 && cells.get(4) != null && !StringUtils.isEmpty((String) cells.get(4))) {
                        organization.setUnifiedCreditCode((String) cells.get(4)); // 社会信用代码
                    } else {
                        organization.setUnifiedCreditCode((String) cells.get(3)); // 社会信用代码
                    }
                }

                organization.setCreateTime(new Date());
                organization.setLastUpdatedBy(new Date());
                organization.setIsActive(true);

                try {
                    String error = ValidateUtil.validate(organization);
                    if (StringUtils.isNotEmpty(error)) {
                        errorList.add("第" + count++ + "行错误:" + error);
                        continue;
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("第" + count++ + "行 validate error:" + e.getMessage());
                    e.printStackTrace();
                    continue;
                }
                logger.debug("#############findOrganizationByParentIdName###################");
                if (organizationService.findOrganizationByParentIdName(organization) > 0) {
                    // 同一层级的组织名称应该不允许重名
                    errorList.add("第" + count++ + "行错误:" + "同一层级的组织机构名称不可重名");
                    continue;
                }
                logger.debug("###################findDeptCountByCode #############");
                if (organizationService.findDeptCountByCode(organization) > 0) {
                    errorList.add("第" + count++ + "行错误:" + "新增失败,该部门已经存在");
                    continue;
                }
                organizationService.insert(organization);
                ++successCount;
                ++count;
            }
            if (newfile.exists()) {
                newfile.delete();
            }
            if (successCount <= 0) {
                return ResultVoUtils.error(500, "导入失败，详情请查看错误信息", errorList);
            }

            String message = "新增成功" + successCount + "个组织机构";
            if (CollectionUtils.isEmpty(errorList)) {
                return ResultVoUtils.ok(message);
            }
            return ResultVoUtils.ok(message + ",其余失败，详情请查看错误信息", errorList);
        } catch (Exception e) {
            logger.error("导入组织信息失败：{}", e.getMessage());
            return ResultVoUtils.error(500, e.getMessage());
        }
    }

    @ApiOperation(value = "查询全部组织", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "/getAllOrgs", method = RequestMethod.GET)
    public ResultVo getAllOrgs(@RequestParam(value = "userId") Long userId) {
        logger.debug("查询全部组织 params :" + userId);
        List<Organization> organizations = organizationService.findAllOrganizations(userId);
        return ResultVoUtils.ok(organizations);
    }

    @ApiOperation(value = "批量删除组织", notes = "如果部门有下级部门不可删除", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "组织ids，以,隔开", required = true, dataType = "Long[]", paramType = "body")
    })
    @RequestMapping(value = "/batchDelete", method = RequestMethod.DELETE)
    public ResultVo batchDelete(@RequestBody List<Long> ids) throws Exception {
        return ResultVoUtils.ok(organizationService.batchDelete(ids));
    }

    @ApiOperation(value = "查询可以选择的所属组织列表", notes = "根据租户区分", httpMethod = "GET")
    @RequestMapping(value = "/findAscriptionDeptList", method = RequestMethod.GET)
    public ResultVo<List<OrganizationVo>> findAscriptionDeptListByParentId(@RequestParam(value = "id", required = false) Long id) {
        List<OrganizationVo> list = organizationService.findAscriptionDeptList(id);
        return ResultVoUtils.ok(list);
    }

}