package com.ys.idatrix.quality.web.controller;

import com.ys.idatrix.quality.analysis.dto.NodeDictDto;
import com.ys.idatrix.quality.service.analysis.CloudAnalysisService;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName DictEsController
 * @Description TODO
 * @Author ouyang
 * @Date 2018/10/17 14:19
 * @Version 1.0
 */
@RequestMapping("/es")
@RestController
public class DictEsController {

    @Autowired
    private CloudAnalysisService cloudAnalysisService;

    /**
     * 根据字典 ID 获取字典中的数据集
     * @param dicId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dict/syncSynonyms/{dicId}", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getSynonymsByDicId(@PathVariable("dicId") String dicId, HttpServletRequest request) throws Exception {

    	long lastModified = System.currentTimeMillis();
    	NodeDictDto dict = cloudAnalysisService.findDictById(dicId);
    	if( dict != null ) {
    		lastModified = dict.getUpdateTime() != null ? dict.getUpdateTime().getTime() : lastModified ;
    	}

        //设置响应头 lastModified 标记
        HttpHeaders headers = new HttpHeaders();
        headers.setLastModified(lastModified);
        headers.set("ETag", Long.toString(lastModified));
        headers.add("Content-Type","text/html; charset=utf-8");

        String result = null;
        String loadData = request.getHeader("canReloadData");
        if( !Utils.isEmpty(loadData) && "YES".equalsIgnoreCase(loadData)){
            //返回响应
            result = cloudAnalysisService.getDictDataString( dicId );
        }
        
        ResponseEntity<String> response = new ResponseEntity<String>(result, headers, HttpStatus.OK);
        return response;
    }

}
