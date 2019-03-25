package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.IServiceService;
import com.idatrix.resource.basedata.vo.ServiceQueryVO;
import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源服务增删改查
 */

@Controller
@RequestMapping("/service")
@Api(value = "/service", tags="服务管理-共享服务管理接口")
public class ServiceController extends BaseController {

    @Autowired
    private IServiceService iServiceService;

    @Autowired
    private UserUtils userUtils;

    private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);

    /**
     * 根据ID查询源服务信息
     */
    @ApiOperation(value = "获取所有共享服务", notes="获取所有共享服务", httpMethod = "GET")
    @RequestMapping("/getAllServices")
    @ResponseBody
    public Result<List<ServiceVO>> getAllService() {
        Long rentId = userUtils.getCurrentUserRentId();
        List<ServiceVO> servicesList = iServiceService.getAllService(rentId);
        return Result.ok(servicesList);
    }

    /**
     * 新增资源服务
     */
    @ApiOperation(value = "增加或修改共享服务", notes="增加或修改共享服务", httpMethod = "POST")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Result saveOrUpdateService(@RequestBody ServiceVO serviceVO) {
        String user = userUtils.getCurrentUserName();
        Long rentId = userUtils.getCurrentUserRentId();
        String errMsg = iServiceService.saveOrUpdateService(rentId, user, serviceVO);

        if ("".equals(errMsg)) {
            return Result.ok("保存或更新操作成功");
        } else {
            return Result.error(errMsg);
        }
    }

    /**
     * 根据ID查询资源服务信息
     */
    @ApiOperation(value = "获取共享服务详情", notes="获取共享服务详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "共享服务ID", required = true, dataType="Long"),
    })
    @RequestMapping("/getServiceById")
    @ResponseBody
    public Result getSourceServiceById(@RequestParam(value = "id", required = true) Long id) {
        ServiceVO serviceVO;
        try {
            serviceVO = iServiceService.getServiceById(id, userUtils.getCurrentUserName());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(serviceVO);
    }

    /**
     * 根据一个或多个ID删除资源服务信息
     */
    @ApiOperation(value = "删除共享服务", notes="删除共享服务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "共享服务ID", required = true, dataType="String"),
    })
    @RequestMapping("/deleteServiceById")
    @ResponseBody
    public Result deleteServiceById(@RequestParam(value = "id") String id) {

        try {
            if (!CommonUtils.isEmptyStr(id)) {
                String[] ids = id.split(",");
                List<Long> idList = new ArrayList<Long>();

                for (int i = 0; i < ids.length; i++) {
                    Long idValue = Long.valueOf(ids[i]);
                    idList.add(idValue);
                }

                String result = iServiceService.deleteServiceByIds(idList);
                if (result != null) {
                    return Result.error("共享服务: " + result + " 已经与已发布的资源绑定，无法删除");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败" + e.getMessage());
        }

        return Result.ok("删除成功");
    }

    /**
     * 分页查询服务信息
     */
    @ApiOperation(value = "查询共享服务", notes="查询共享服务", httpMethod = "GET")
    @RequestMapping("/getAllServicePages")
    @ResponseBody
    public Result getServicesByCondition(ServiceQueryVO queryVO) {
        queryVO.setRentId(userUtils.getCurrentUserRentId());
        try {
            ResultPager resultPager
                    = iServiceService.getServicesByCondition(queryVO);
            return Result.ok(resultPager);
        } catch (Exception e) {
            LOG.error(e.toString());
            return Result.error(e.getMessage());
        }
    }

    /*根据ID查询资源服务信息*/
    @ApiOperation(value = "根据URL获取WSDL信息", notes="根据URL获取WSDL信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "地址信息", required = true, dataType="String"),
    })
    @RequestMapping("/getWSDLContents")
    @ResponseBody
    public Result getWSDLContentsByRemoteAddress(@RequestParam(value = "url") String url) {

        try {
            String content = CommonUtils.getWSDLContentsByRemoteAddress(url);
            return Result.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
