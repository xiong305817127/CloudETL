package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHiveService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHiveVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HIVE;

/**
 * 元数据管理-元数据定义-hive处理接口
 * @author robin
 *
 */

@Validated
@Slf4j
@RestController
@RequestMapping("/metadef/hive")
@Api(value = "/metadef/hive" , tags="元数据管理-元数据定义-Hive处理接口")
public class MetaDefHiveController {

    @Autowired
    private IMetaDefHiveService metaDefHiveService;

    /**
     * 元数据定义-HIVE定义编辑首页
     * @param searchVO
     * @return
     */
    @ApiOperation(value = "元数据定义-Hive首页", notes="元数据定义-Hive首页", httpMethod = "POST")
    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<PageResultBean<MetaDefOverviewVO>> getOverview(@RequestBody MetadataSearchVo searchVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        searchVO.setRenterId(rentId);
        searchVO.setDatabaseType(HIVE.getCode());
        PageResultBean defVO = null;
        try {
            defVO = metaDefHiveService.hiveQueryOverview(searchVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(defVO);
    }


    /**
     * 元数据定义-HIVE存为草稿
     * @param baseVO
     * @return
     */
    @ApiOperation(value = "元数据定义-Hive存为草稿", notes="元数据定义-Hive存为草稿,", httpMethod = "POST")
    @RequestMapping(value="/saveDraft", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<Metadata> saveDraft(@RequestBody MetaDefHiveVO baseVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata defVO = null;
        try {
            defVO = metaDefHiveService.saveDraft(rentId ,user, baseVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return ResultBean.ok(defVO);
    }

    /**
     * 元数据定义-HIVE保存生效
     * @param baseVO
     * @return
     */
    @ApiOperation(value = "元数据定义-Hive保存生效", notes="元数据定义-Hive保存生效", httpMethod = "POST")
    @RequestMapping(value="/saveExec", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<Metadata> saveExec(@RequestBody MetaDefHiveVO baseVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata defVO = null;
        try {
            defVO = metaDefHiveService.saveExec(rentId ,user, baseVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return ResultBean.ok(defVO);
    }


    /**
     * 元数据定义-HIVE删除
     * @param id
     * @return
     */
    @ApiOperation(value = "元数据定义-Hive删除", notes="元数据定义-Hive删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="元数据定义ID", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping(value="/delete", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean<Long> delete(@RequestParam(value = "id") Long[] id) {
        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Long deleteId = null;
        if(id==null || ArrayUtils.isEmpty(id)){
            ResultBean.error("传入删除参数为空");
        }
        try {
            Arrays.asList(id).stream().forEach( idEach -> {
                metaDefHiveService.delete(rentId, user, idEach);
            });
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok("true");
    }


    /**
     * 元数据定义-HIVE获取详情
     * @param id
     * @return
     */
    @ApiOperation(value = "元数据定义-获取Hive详情", notes="元数据定义-获取Hive详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="元数据定义ID", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping(value="/getDetail", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean<MetaDefHiveVO> getDetail(@RequestParam(value = "id") Long id) {
        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        MetaDefHiveVO hiveVO = null;
        try {
            hiveVO = metaDefHiveService.getDetail(rentId, user, id);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(hiveVO);
    }
}
