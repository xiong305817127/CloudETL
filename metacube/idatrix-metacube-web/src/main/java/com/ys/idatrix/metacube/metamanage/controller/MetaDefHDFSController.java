package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHDFSService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHDFSVO;
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

import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HDFS;


/**
 * 元数据管理-元数据定义-HDFS功能
 * @author robin
 *
 */

@Validated
@Slf4j
@RestController
@RequestMapping("/metadef/hdfs")
@Api(value = "/metadef/hdfs" , tags="元数据管理-元数据定义-HDFS处理接口")
public class MetaDefHDFSController {

    @Autowired
    private IMetaDefHDFSService metaDefHDFSService;

    /**
     * 元数据定义-HDFS定义编辑首页
     * @param searchVO
     * @return
     */
    @ApiOperation(value = "元数据定义-HDFS首页", notes="元数据定义-HDFS首页,传递参数schemaId,status,如果需要查询所有状态" +
            "，则不传递status数值", httpMethod = "POST")
    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<PageResultBean<MetaDefOverviewVO>> getOverview(@RequestBody MetadataSearchVo searchVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        searchVO.setRenterId(rentId);
        searchVO.setDatabaseType(HDFS.getCode());
        PageResultBean defVO = null;
        try {
            defVO = metaDefHDFSService.hdfsQueryOverview(searchVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(defVO);
    }


    /**
     * 元数据定义-HDFS存为草稿
     * @param baseVO
     * @return
     */
    @ApiOperation(value = "元数据定义-HDFS存为草稿", notes="元数据定义-HDFS存为草稿，保存时候需要传递rootPath根路径", httpMethod = "POST")
    @RequestMapping(value="/saveDraft", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<Metadata> saveDraft(@RequestBody MetaDefHDFSVO baseVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata defVO = null;
        try {
            defVO = metaDefHDFSService.saveDraft(rentId ,user, baseVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return ResultBean.ok(defVO);
    }

    /**
     * 元数据定义-HDFS保存生效
     * @param baseVO
     * @return
     */
    @ApiOperation(value = "元数据定义-HDFS保存生效", notes="元数据定义-HDFS保存生效，保存时候需要传递rootPath根路径", httpMethod = "POST")
    @RequestMapping(value="/saveExec", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<Metadata> saveExec(@RequestBody MetaDefHDFSVO baseVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata defVO = null;
        try {
            defVO = metaDefHDFSService.saveExec(rentId ,user, baseVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return ResultBean.ok(defVO);
    }


    /**
     * 元数据定义-HDFS删除
     * @param id
     * @return
     */
    @ApiOperation(value = "元数据定义-HDFS删除", notes="元数据定义-HDFS删除，根据ID来删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="元数据定义ID", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping(value="/delete", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean<Long[]> delete(@RequestParam(value = "id") Long[] id) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Long deleteId = null;
        if(id==null || ArrayUtils.isEmpty(id)){
            ResultBean.error("传入删除参数为空");
        }
        try {
            Arrays.asList(id).stream().forEach(idEach -> {
                metaDefHDFSService.delete(rentId, user, idEach);
            });
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(id);
    }


    /**
     * 元数据定义-HDFS删除
     * @param id
     * @return
     */
    @ApiOperation(value = "元数据定义-HDFS获取详情", notes="元数据定义-HDFS获取详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="元数据定义ID", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping(value="/getDetail", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean<Metadata> getDetail(@RequestParam(value = "id") Long id) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata data = null;
        try {
            data = metaDefHDFSService.getDetail(rentId, user, id);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(data);
    }
}
