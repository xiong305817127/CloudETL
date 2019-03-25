package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.exception.DictionaryDeleteException;
import com.idatrix.resource.basedata.service.IDictionaryService;
import com.idatrix.resource.basedata.vo.DictionaryListVO;
import com.idatrix.resource.basedata.vo.DictionaryVO;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.idatrix.resource.basedata.common.BasedataConstant.*;

/**
 * 系统数据字典管理
 */
@Controller
@RequestMapping("dictionary")
@Api(value = "/dictionary" , tags="基础功能-数据字典管理接口")
public class DictionaryController {


    @Autowired
    private UserUtils userUtils;

    @Autowired
    private IDictionaryService dictionaryService;

    /**
     *  资源配置里面使用-资源类型获取
     * @return
     */
    @ApiOperation(value = "资源配置里面使用-资源类型获取", notes="资源配置里面使用-资源类型获取", httpMethod = "GET")
    @RequestMapping("/getResourceTypeDict")
    @ResponseBody
    public Result getResourceTypeDict() {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        //type classify 资源格式分类，db 数据库资源格式分类
        List<DictionaryVO> servicesList = dictionaryService.getResourceTypeDict(rentId, user);
        return Result.ok(servicesList);
    }

    /**
     *  查询字典的子节点ID配置
     * @return
     */
    @ApiOperation(value = "资源配置里面使用-共享方式获取", notes="资源配置里面使用-资源类型获取", httpMethod = "GET")
    @RequestMapping("/getResourceShareDict")
    @ResponseBody
    public Result getResourceShareDict() {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        //type classify 资源格式分类，db 数据库资源格式分类
        List<DictionaryVO> servicesList = dictionaryService.getResourceShareDict(rentId, user);
        return Result.ok(servicesList);
    }


    /**
     *  查询字典的子节点ID配置
     * @param id 字典ID
     * @return
     */
    @ApiOperation(value = "资源配置里面使用-查询字典的子选项", notes="资源配置里面使用-查询字典的子选项", httpMethod = "GET")
    @RequestMapping("/getResourceChildDict")
    @ResponseBody
    public Result getChildDict(@ApiParam(value = "根据父节点字典ID获取子字典", required = true)@RequestParam(value = "id", required = true) Long id) {
        Long rentId = userUtils.getCurrentUserRentId();
        //type classify 资源格式分类，db 数据库资源格式分类
        List<DictionaryVO> servicesList = dictionaryService.getAllDictionaryInfoByParendId(rentId, id);
        return Result.ok(servicesList);
    }

    /**
     *  查询字典的子节点ID配置
     * @param type classify 资源分类/type 资源格式/share 共享方式
     * @return
     */
    @ApiOperation(value = "资源配置里面使用-根据类型查询字典", notes="资源配置里面使用-查询字典的子选项", httpMethod = "GET")
    @RequestMapping("/getResourceDict")
    @ResponseBody
    public Result getResourceDict(@ApiParam(value = "classify 资源分类/type 资源格式/share 共享方式", required = true)@RequestParam(value = "type", required = true) String type) {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        //type classify 资源格式分类，db 数据库资源格式分类
        List<DictionaryVO> servicesList = dictionaryService.getResourceAllDictionaryInfo(rentId, user, type);
        return Result.ok(servicesList);
    }

    /**
     *  查询字典
     * @param type classify 资源分类/type 资源格式/share 共享方式
     * @return
     */
    @ApiOperation(value = "查询字典不同类型字典", notes="获取现有字典列表", httpMethod = "GET")
    @RequestMapping("/getDict")
    @ResponseBody
    public Result getDict(@ApiParam(value = "classify 资源分类/type 资源格式/share 共享方式", required = true)@RequestParam(value = "type", required = true) String type) {
        Long rentId = userUtils.getCurrentUserRentId();
        //type classify 资源格式分类，db 数据库资源格式分类
        List<DictionaryVO> servicesList = dictionaryService.getAllDictionaryInfo(rentId, type);
        return Result.ok(servicesList);
    }


    /**
     *  增加数据分类字典
     * @param dictionaryListVO
     * @return
     */
    @ApiOperation(value = "增加数据分类字典", notes="为数据分类型字典", httpMethod = "POST")
    @RequestMapping(value="/addClassify", method= RequestMethod.POST)
    @ResponseBody
    public Result addClassifyDict(@RequestBody DictionaryListVO dictionaryListVO) {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        try {
            dictionaryService.addDictionary(rentId, user, CLASSIFY_DICT, dictionaryListVO.getDictionaryVO());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /**
     *  增加数据资源格式
     * @param dictionaryListVO
     * @return
     */
    @ApiOperation(value = "增加数据资源格式", notes="为数据格式类型字典", httpMethod = "POST")
    @RequestMapping(value="/addType", method= RequestMethod.POST)
    @ResponseBody
    public Result addType(@RequestBody DictionaryListVO dictionaryListVO) {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        try {
            dictionaryService.addDictionary(rentId, user, TYPE_DICT, dictionaryListVO.getDictionaryVO());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /**
     *  增加数据共享资源记录
     * @param dictionaryListVO
     * @return
     */
    @ApiOperation(value = "增加数据共享资源", notes="为数据共享类型字典", httpMethod = "POST")
    @RequestMapping(value="/addShare", method= RequestMethod.POST)
    @ResponseBody
    public Result addShare(@RequestBody DictionaryListVO dictionaryListVO) {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        try {
            dictionaryService.addDictionary(rentId, user, SHARE_DICT, dictionaryListVO.getDictionaryVO());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }


    /**
     *  删掉某个数据分类字典
     * @param ids
     * @return
     */
    @ApiOperation(value = "强制删除分类字典", notes="删除资源的时候，首先调用 /delete接口，返回异常提示界面，点击确认，异常编码为'2100'，调用/forceDelete", httpMethod = "GET")
    @RequestMapping("/delete")
    @ResponseBody
    public Result delete(@ApiParam(value = "字典ID", required = true)@RequestParam(value = "id", required = true) Long[] ids) {
        try {
            dictionaryService.deleteDictionaryById(ids, null);
        }catch (DictionaryDeleteException e1){
            e1.printStackTrace();
            return Result.error(e1.getErrorCode().toString(), e1.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /**
     *  强制删除分类字典
     * @param ids
     * @return
     */
    @ApiOperation(value = "强制删除分类字典", notes="删除资源的时候，首先调用 /delete接口，返回异常提示界面，点击确认，调用/forceDelete", httpMethod = "GET")
    @RequestMapping("/forceDelete")
    @ResponseBody
    public Result forceDelete(@ApiParam(value = "字典ID", required = true)@RequestParam(value = "id", required = true) Long[] ids) {
        try {
            dictionaryService.deleteDictionaryById(ids, "force");
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }




}
