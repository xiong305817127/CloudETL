package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHBaseService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHbaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HBASE;


/**
 * 元数据定义-Hbase处理
 * @author robin
 *
 */

@Validated
@Slf4j
@RestController
@RequestMapping("/metadef/hbase")
@Api(value = "/metadef/hbase" , tags="元数据管理-元数据定义-HBase处理接口")
public class MetaDefHBaseController {

    @Autowired
    private IMetaDefHBaseService metaDefHBaseService;

    /**
     * 元数据定义-HIVE定义编辑首页
     * @param searchVO
     * @return
     */
    @ApiOperation(value = "元数据定义-HBase首页", notes="元数据定义-HBase首页", httpMethod = "POST")
    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<PageResultBean<MetaDefOverviewVO>> getOverview(@RequestBody MetadataSearchVo searchVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        searchVO.setRenterId(rentId);
        searchVO.setDatabaseType(HBASE.getCode());
        PageResultBean defVO = null;
        try {
            defVO = metaDefHBaseService.hbaseQueryOverview(searchVO);
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
    @ApiOperation(value = "元数据定义-HBase存为草稿", notes="元数据定义-HBase存为草稿,", httpMethod = "POST")
    @RequestMapping(value="/saveDraft", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<Metadata> saveDraft(@RequestBody MetaDefHbaseVO baseVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata defVO = null;
        try {
            defVO = metaDefHBaseService.saveDraft(rentId ,user, baseVO);
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
    @ApiOperation(value = "元数据定义-HBase保存生效", notes="元数据定义-HBase保存生效", httpMethod = "POST")
    @RequestMapping(value="/saveExec", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean<Metadata> saveExec(@RequestBody MetaDefHbaseVO baseVO) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Metadata defVO = null;
        try {
            defVO = metaDefHBaseService.saveExec(rentId ,user, baseVO);
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
    @ApiOperation(value = "元数据定义-HBase删除", notes="元数据定义-HBase删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="元数据定义ID", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping(value="/delete", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean<Long> delete(@RequestParam(value = "id") Long id) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        Long deleteId = null;
        try {
            deleteId = metaDefHBaseService.delete(rentId, user, id);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(id);
    }


    /**
     * 元数据定义-HIVE获取详情
     * @param id
     * @return
     */
    @ApiOperation(value = "元数据定义-获取HBase详情", notes="元数据定义-获取HBase详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="元数据定义ID", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping(value="/getDetail", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean<MetaDefHbaseVO> getDetail(@RequestParam(value = "id") Long id) {

        String user = UserUtils.getUserName();
        Long rentId = UserUtils.getRenterId();
        MetaDefHbaseVO hbaseVO = null;
        try {
            hbaseVO = metaDefHBaseService.getDetail(rentId, user, id);
        }catch(Exception e){
            e.printStackTrace();
            return ResultBean.error(e.getMessage());
        }
        return ResultBean.ok(hbaseVO);
    }

}
