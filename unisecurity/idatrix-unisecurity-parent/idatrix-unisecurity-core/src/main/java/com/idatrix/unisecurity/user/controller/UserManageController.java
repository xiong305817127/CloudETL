package com.idatrix.unisecurity.user.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.domain.ImportMsg;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.domain.UserData;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.*;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.session.CustomSessionManager;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;
import com.idatrix.unisecurity.organization.service.OrganizationService;
import com.idatrix.unisecurity.properties.LoginProperties;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.service.SynchUserToSsz;
import com.idatrix.unisecurity.user.service.UUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/member")
@Api(value = "/memberController", description = "安全管理-用户管理API（主要是租户管理用户所使用）")
public class UserManageController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private CustomSessionManager customSessionManager;

    @Autowired(required = false)
    private UUserService userService;

    @Autowired(required = false)
    private OrganizationService organizationService;

    @Autowired(required = false)
    private FreeIPATemplate freeIPATemplate;

    @Autowired(required = false)
    private LdapHttpDataBuilder ldapHttpDataBuilder;

    @Autowired
    private LdapMgrUserGroupBuilder ldapMgrUserGroupBuilder;

    @Autowired(required = false)
    private Config config;

    @Autowired
    private SynchUserToSsz synchUserToSsz;

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private LoginProperties loginProperties;

    @ApiOperation(value = "查询当前租户下的用户列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "当前显示第几页", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "当前显示多少条数据", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "findContent", value = "搜索key", dataType = "int", paramType = "query")
    })
    @RequestMapping(value = "/users")
    public ResultVo users(ModelMap map, @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                          @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String findContent) {
        UUser user = ShiroTokenManager.getToken();
        map.put("findContent", findContent);
        map.put("renterId", user.getRenterId());
        map.put("id", user.getId());
        Pagination<UUser> page = userService.findByPage(map, pageNo, pageSize);
        return ResultVoUtils.ok(page);
    }

    @ApiOperation(value = "新增用户", notes = "")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultVo addMember(UUser uUser) throws Exception {
        if (userService.findUserByUsername(uUser.getUsername()) > 0) {
            throw new SecurityException(ResultEnum.USER_ACCOUNT_EXIST.getCode(), "当前用户账号已经存在，请修改！！！");
        } else if (userService.findUserByEmail(uUser.getEmail()) > 0) {
            throw new SecurityException(ResultEnum.EMAIL_EXIST.getCode(), "当前邮箱已经被注册，请修改！！！");
        } else if (userService.findUserByPhone(uUser.getPhone()) > 0) {
            throw new SecurityException(ResultEnum.PHONE_EXIST.getCode(), "用户手机号已经被注册，请修改！！！");
        } else {
            UUser user = ShiroTokenManager.getToken();
            uUser.setRenterId(user.getRenterId());
            uUser.setDeptId(user.getDeptId()); // 默认与租户Id 相同
            userService.insertSelective(uUser);
            UUser u = userService.getUser(uUser.getUsername());
            try {
                // 同步增加freeipa用户
                addLdapUser(String.valueOf("u_" + u.getId()), uUser.getPswd(), uUser.getDeptId());
            } catch (Exception e) {
                logger.error("add user synchronized freeipa error。user id：" + user.getId());
                e.printStackTrace();
            }

            // TODO hdfsUnrestrictedDao.createPlatformUserDir(owner, dirPath);
            //createPlatformUserDir();
        }
        return ResultVoUtils.ok("新增成功！！！");
    }

    //新增用户同步到 freeipa 中
    private void addLdapUser(String name, String password, Long deptId) throws Exception {
        if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
            FreeIPAProxyImpl impl = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
            impl.addUser(name, password);
            List<String> usrs = new ArrayList<String>();
            usrs.add(name);
            impl.addUsers2Group(usrs, "d_" + deptId);
        }
    }

    @ApiOperation(value = "修改用户信息", notes = "")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultVo updateMember(UUser entity) throws Exception {
        // 唯一性校验
        UUser persistUsr = userService.selectByPrimaryKey(entity.getId());

        if (persistUsr == null) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "用户不存在");
        }

        if (entity.getPhone() != null && !entity.getPhone().equals(persistUsr.getPhone())) {
            int count = userService.findUserByPhone(entity.getPhone());
            if (count > 0) {
                throw new SecurityException(ResultEnum.PHONE_EXIST.getCode(), "修改失败，该手机已经存在");
            }
        }

        if (entity.getEmail() != null && !entity.getEmail().equals(persistUsr.getEmail())) {
            int count = userService.findUserByEmail(entity.getEmail());
            if (count > 0) {
                throw new SecurityException(ResultEnum.EMAIL_EXIST.getCode(), "修改失败，该邮箱已经存在");
            }
        }

        /*String password = null;
        // 如果密码不为空
        if (entity.getPswd() != null) {
            // 先解密
            password = EncryptUtil.getInstance().strDec(entity.getPswd(), entity.getId().toString(), entity.getRealName(), null);
            entity.setPswd(password);
            entity = UserManager.md5Pswd(entity);
        }*/
        userService.updateByPrimaryKeySelective(entity);

        return ResultVoUtils.ok("修改成功！！！");
    }

    @ApiOperation(value = "批量删除用户信息", notes = "")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultVo deleteMember(@ApiParam(name = "ids", value = "用户ids，以，分割") String ids) throws Exception {
        return ResultVoUtils.ok("成功删除" + userService.deleteUserById(ids) + "条数据");
    }

    /**
     * 改变状态（暂时没看到使用，不做修改）
     *
     * @param [status, sessionIds]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author oyr
     * @date 2018/9/6 9:52

     @ApiIgnore
     @RequestMapping(value = "/changeSessionStatus", method = RequestMethod.POST)
     public Map<String, Object> changeSessionStatus(Boolean status, String sessionIds) {
     return customSessionManager.changeSessionStatus(status, sessionIds);
     }*/

    /**
     * 禁止登录（暂时没看到使用，不做修改）
     *
     * @param [ids, status]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author oyr
     * @date 2018/9/6 9:52
     * @ApiIgnore
     * @RequestMapping(value = "/forbidUserById", method = RequestMethod.POST)
     * public Map<String, Object> forbidUserById(Long id, Long status) {
     * return userService.updateForbidUserById(id, status);
     * }
     */

    @ApiOperation(value = "导出用户信息", notes = "")
    @RequestMapping(value = "export", method = RequestMethod.GET)
    public ResultVo exportUser(String ids, HttpServletResponse response) {
        Map resultMap = ResultVoUtils.resultMap();
        List<UserData> users = userService.export(ids);
        String[] titles = {"用户名", "真实姓名", "性别", "年龄", "电子邮箱", "身份证号码", "手机号码"};
        WriteExcel<UserData> writeExcel = new WriteExcel<>();
        writeExcel.createExcel("用户信息表", titles, users, response);
        return ResultVoUtils.ok("导出用户信息成功");
    }

    @ApiOperation(value = "导入用户", notes = "")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResultVo importUser(@NotNull(message = "文件不能为空") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("importUser start paras : file name=" + file.getOriginalFilename());

        UUser loginUser = ShiroTokenManager.getToken();
        String fileName = file.getOriginalFilename();
        String batchId = SecurityStringUtils.getRandom(10);
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (!"xls".equals(fileExtName) && !"xlsx".equals(fileExtName)) {
            userService.insertErrLog(batchId, fileName, null, "不支持的文件类型");
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "不支持的文件类型");
        }

        String path = request.getServletContext().getRealPath("/upload/excel");
        File newfile = new File(path + "/" + fileName);
        FileOutputStream fos = FileUtils.openOutputStream(newfile);
        IOUtils.copy(file.getInputStream(), fos);
        file.getInputStream().close();
        fos.flush();
        fos.close();
        UUser user = null;
        List<List<Object>> dataSet = ReadExcel.readExcel(newfile);
        int count = 0;
        List<String> existUsers = new ArrayList<String>();
        List<String> errorList = new ArrayList<String>();
        List<String> userNames = new ArrayList<String>();
        for (int row = 0; row < dataSet.size(); row++) {
            if (CollectionUtils.isEmpty(dataSet.get(row))) {
                throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "表格无数据！！！");
            } else {
                List<Object> datarow = dataSet.get(row);
                ImportMsg importMsg = new ImportMsg(batchId, fileName,
                        StringUtils.isEmpty(String.valueOf(datarow.get(0))) ? "" : String.valueOf(datarow.get(0)),
                        StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(1))) ? "" : String.valueOf(datarow.get(1)),
                        StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(2))) ? "" : String.valueOf(datarow.get(2)),
                        "男".equals(dataSet.get(row).get(3)) ? 1 : "女".equals(dataSet.get(row).get(3)) ? 2 : 3,
                        dataSet.get(row).get(4) == null ? null : Integer.parseInt(String.valueOf(dataSet.get(row).get(4))),
                        StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(5))) ? "" : String.valueOf(dataSet.get(row).get(5)),
                        StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(6))) ? "" : String.valueOf(dataSet.get(row).get(6)),
                        StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(7))) ? "" : String.valueOf(dataSet.get(row).get(7)));

                if (userService.findUserByUsername(String.valueOf(dataSet.get(row).get(0))) > 0) {
                    errorList.add("第" + (row + 1) + "行用户已经存在，添加失败");
                    logger.debug("第" + (row + 1) + "行用户已经存在，添加失败");
                    existUsers.add(String.valueOf(dataSet.get(row).get(0)));
                    // add err log
                    importMsg.setMsg("第" + (row + 1) + "行用户已经存在，添加失败");
                    userService.insertErrLog(importMsg);
                } else {
                    // 校验
                    user = new UUser();
                    user.setRenterId(loginUser.getRenterId());
                    user.setUsername(StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(0))) ? "" : String.valueOf(dataSet.get(row).get(0)));
                    user.setRealName(StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(2))) ? "" : String.valueOf(dataSet.get(row).get(2)));
                    String sex = String.valueOf(dataSet.get(row).get(3));
                    if ("男".equals(sex.trim()))
                        user.setSex(1L);
                    else if ("女".equals(sex.trim()))
                        user.setSex(2L);
                    else
                        user.setSex(3L);
                    user.setAge(dataSet.get(row).get(4) == null ? null : Integer.parseInt(String.valueOf(dataSet.get(row).get(4))));
                    if (user.getAge() < 0) {
                        errorList.add("第" + (row + 1) + "行错误:" + "年龄不能为负数");
                        logger.debug("第" + (row + 1) + "行错误:" + "年龄不能为负数");
                        importMsg.setMsg("第" + (row + 1) + "行错误:" + "年龄不能为负数");
                        userService.insertErrLog(importMsg);
//                        	userService.insertErrLog(batchId,fileName,user.getUsername(),"第"+(row+1)+"行错误:"+"年龄不能为负数");
                        continue;
                    }
                    user.setEmail(StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(5))) ? "" : String.valueOf(dataSet.get(row).get(5)));
                    user.setCardId(StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(6))) ? "" : String.valueOf(dataSet.get(row).get(6)));
                    String phone = String.valueOf(dataSet.get(row).get(7));
                    if (StringUtils.isNotEmpty(phone)) {
                    }
                    user.setPhone(StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(7))) ? "" : String.valueOf(dataSet.get(row).get(7)));
                    user.setCreateTime(new Date());
                    user.setLastUpdatedDate(new Date());
                    user.setStatus(1L);
                    user.setPswd(StringUtils.isEmpty(String.valueOf(dataSet.get(row).get(1))) ? "" : String.valueOf(dataSet.get(row).get(1)));

                    if (userService.findUserByEmail(user.getEmail()) > 0) {
                        errorList.add("第" + (row + 1) + "行邮箱已经被注册，添加失败");
                        logger.debug("第" + (row + 1) + "行邮箱已经被注册，添加失败");
                        existUsers.add(String.valueOf(dataSet.get(row).get(0)));
                        // add err log
                        importMsg.setMsg("第" + (row + 1) + "行邮箱已经被注册，添加失败");
                        userService.insertErrLog(importMsg);

//                            userService.insertErrLog(batchId,fileName,String.valueOf(dataSet.get(row).get(0)),"第"+(row+1)+"行邮箱已经被注册，添加失败");
                        continue;
                    } else if (userService.findUserByPhone(user.getPhone()) > 0) {
                        errorList.add("第" + (row + 1) + "行手机已经被注册，添加失败");
                        logger.debug("第" + (row + 1) + "行手机已经被注册，添加失败");
                        existUsers.add(String.valueOf(dataSet.get(row).get(0)));
                        // add err log
                        importMsg.setMsg("第" + (row + 1) + "行手机已经被注册，添加失败");
                        userService.insertErrLog(importMsg);
//                            userService.insertErrLog(batchId,fileName,String.valueOf(dataSet.get(row).get(0)),"第"+(row+1)+"行手机已经被注册，添加失败");
                        continue;
                    }

                    try {
                        String error = ValidateUtil.validate(user);
                        if (StringUtils.isNotEmpty(error)) {
                            errorList.add("第" + (row + 1) + "行错误:" + error);
                            logger.info("第" + (row + 1) + "行错误:" + error);
                            importMsg.setMsg("第" + (row + 1) + "行错误:" + error);
                            userService.insertErrLog(importMsg);
//								userService.insertErrLog(batchId,fileName,user.getUsername(),"第"+(row+1)+"行错误:"+error);
                            continue;
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        logger.error("第" + (row + 1) + "行 validate error:" + e.getMessage());

                        importMsg.setMsg("第" + (row + 1) + "行 validate error:" + e.getMessage());
                        userService.insertErrLog(importMsg);
//							userService.insertErrLog(batchId,fileName,user.getUsername(),"第"+(row+1)+"行错误:"+e.getMessage().substring(0,100));
                        continue;
                    }
                    user.setDeptId(loginUser.getDeptId());
                    userService.insertSelective(user);
                    userNames.add(user.getUsername());
                    ++count;
                }
            }
        }

        if (newfile.exists()) {
            newfile.delete();
        }

        Map map = ResultVoUtils.resultMap();
        if (CollectionUtils.isNotEmpty(errorList)) {
            map.put("err_link", "/member/exportErrLog.shtml?batch_id=" + batchId);
        }
        String message = "导入成功" + count + "条";
        if (count != dataSet.size()) {
            int failCount = dataSet.size() - count;
            message += "；导入失败" + failCount + "条，导入失败的记录请点击“导出错误记录”查看失败原因";
        }
        map.put("message", message);

        //TODO add ladp
        List<UUser> list = userService.getUsersByuserNames(userNames);

        return ResultVoUtils.ok(map);
    }

    @ApiOperation(value = "导出错误信息", notes = "")
    @RequestMapping(value = "/exportErrLog", method = RequestMethod.GET)
    public ResultVo exportMsg(@NotBlank(message = "信息id不能为空") @RequestParam("batch_id") String batchId,
                              HttpServletRequest request, HttpServletResponse response) {
        logger.debug("exportMsg begin batch :" + batchId);
        Map resultMap = ResultVoUtils.resultMap();
        List<ImportMsg> messages = userService.exportImportMsg(batchId);
        if (CollectionUtils.isEmpty(messages)) {
            return ResultVoUtils.ok("当前错误信息为空");
        }

        for (ImportMsg message : messages) {
            if (message.getSex() == 1) {
                message.setSexStr("男");
            } else {
                message.setSexStr("女");
            }
        }

        logger.debug("exportMsg begin msg size :" + messages.size());
        String[] titles = {"用户名", "密码", "真实姓名", "性别", "年龄", "电子邮箱", "身份证号码", "手机号码", "错误信息"};
        String[] keys = {"userName", "password", "realName", "sexStr", "age", "email", "cardId", "phone", "msg"};
        WriteExcel<ImportMsg> writeExcel = new WriteExcel<ImportMsg>();

        writeExcel.createExcel("用户导入错误信息表", titles, keys, messages, response);
        return ResultVoUtils.ok("导出错误信息成功");
    }

    @ApiOperation(value = "查询组织下面的用户", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deptId", value = "组织ID", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "findUsersByOrganizatioId", method = RequestMethod.POST)
    public ResultVo findUsersByOrganizatioId(@NotNull(message = "组织id不能为空") Long deptId) throws Exception {
        logger.debug("findUsersByOrganizatioId start :" + JSON.toJSONString(deptId));

        // 根据 deptId递归查询所有子部门
        List<Long> deptIds = new ArrayList<Long>();
        deptIds.add(deptId);
        getChildDeptIds(deptId, deptIds);
        logger.debug("depts :" + JSON.toJSONString(deptIds));

        return ResultVoUtils.ok(userService.findUsersByOrganizationIds(deptIds));
    }

    private void getChildDeptIds(Long p_deptId, List<Long> deptIds) {
        if (CollectionUtils.isEmpty(getChildDeptIdsBydeptId(p_deptId))) {
            deptIds.add(p_deptId);
        } else {
            List<Long> temps = getChildDeptIdsBydeptId(p_deptId);
            deptIds.addAll(temps);
            for (Long deptId : temps) {
                getChildDeptIds(deptId, deptIds);
            }
        }
    }

    private List getChildDeptIdsBydeptId(Long deptId) {
        return organizationService.getChildDeptIdsByDeptId(deptId);
    }

    @ApiOperation(value = "查询角色下面的用户", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "findUsersByRoleId", method = RequestMethod.POST)
    public ResultVo findUsersByRoleId(@NotNull(message = "角色id不能为空") Long roleId) throws Exception {
        logger.debug("findUsersByRoleId start :" + JSON.toJSONString(roleId));
        List<UUser> list = userService.findUsersByRoleId(roleId);
        return ResultVoUtils.ok(list);
    }

    @ApiOperation(value = "一键导入所有用户到ssz中", notes = "提供给ssz同步使用")
    @RequestMapping(value = "/importAllUserToSsz", method = RequestMethod.POST)
    public ResultVo importAllUserToSsz() {
        synchUserToSsz.importAll();
        return ResultVoUtils.ok();
    }

    @ApiIgnore
    @ApiOperation(value = "获取所有用户信息", notes = "提供给bbs同步用户使用")
    @RequestMapping(value = "/user/all")
    public ResultVo userAll() {
        List<UUser> userList = userService.findAll();
        return ResultVoUtils.ok(userList);
    }

    @ApiOperation(value = "清空redis中用户锁定的标识", notes = "这里的锁定是登录次数锁定，并不是禁止")
    @RequestMapping("/clearUserLock/{name}")
    public ResultVo clearRedisUserLock(@PathVariable String name){
        // 清空登录计数
        jedisClient.del(loginProperties.getUserLoginCountKey() + name);
        // 设置未锁定状态
        jedisClient.del(loginProperties.getUserIsLockKey() + name);
        return ResultVoUtils.ok();
    }

}