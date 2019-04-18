package com.ys.idatrix.db.test;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.dto.FileQueryDto;
import com.ys.idatrix.db.api.hdfs.service.HdfsService;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: HBaseServiceTest
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HdfsServiceTest {

    @Autowired
    private HdfsUnrestrictedService hdfsUnrestrictedService;

    @Autowired
    private HdfsService hdfsService;

    @Test
    public void testCreateDir(){
        RespResult<Boolean> result =  hdfsUnrestrictedService.createDir(null,"/zhj_dir2");
        System.out.println(JSON.toJSONString(result,true));
    }

    @Test
    public void testGetFileList(){
        RespResult<List<FileQueryDto>> result = hdfsService.getListFiles("wzl1","/data/etl61insertdd");
        System.out.println(JSON.toJSONString(result,true));
    }

    @Test
    public void testUploadFileByStream(){
        File file = new File("E:\\02-项目文档\\01-需求\\平台缺少的需求.docx");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            RespResult<Boolean> result = hdfsUnrestrictedService.uploadFileByStream("hdfs://zhoujian_test/平台缺少的需求4.docx", inputStream);
            System.out.println(JSON.toJSONString(result));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
