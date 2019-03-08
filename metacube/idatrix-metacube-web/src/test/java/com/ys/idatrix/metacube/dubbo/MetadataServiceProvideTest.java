package com.ys.idatrix.metacube.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.metacube.api.service.MetadataServiceProvide;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
//@SpringBootTest
public class MetadataServiceProvideTest {

    @Reference //(url = "10.0.0.116:20779")
    private MetadataServiceProvide metadataServiceProvide;

    @Test
    public void testFindHDFSId() {
//        MetadataDTO metadata = metadataServiceProvide.findHDFSId("/wzl/test/tmp");
//        log.info("查询到文件信息: {}", metadata);
    }

    @Test
    public void testFindHDFSFolderByUser(){
//        List<String> folderList = metadataServiceProvide.findHDFSFolderByUser("etl63", ALL);
//        if(CollectionUtils.isEmpty(folderList)){
//            log.info("文件列表为空");
//        } else {
//            log.info("文件列表: {}", folderList.toString());
//        }
    }

    @Test
    public void testStream() {
        List<String> streamList = Arrays.asList("1", "2", "3", "4", "5", "6");
        log.info("列表 {} ，", streamList);
        Long count = streamList.stream().filter(p -> {
            return Long.valueOf(p).equals(6L);
        }).count();
        log.info("数量 {} ,", count);
        streamList.stream().filter(p -> Long.valueOf(p).equals(6L)).forEach(k -> {
            log.info("数据内容 {}", k);
        });
        List<String> rr = streamList.stream()
                .filter(p -> Long.valueOf(p).equals(6L))
                .map(p -> p + "123")
                .collect(Collectors.toList());
        log.info("返回数据 {},", rr.toString());

        List<String> names = new ArrayList<>();
        names.add("TaoBao");
        names.add("ZhiFuBao");
        names.stream().map(name -> name.charAt(0)).collect(Collectors.toList());
    }

    @Test
    public void testArrrayList(){

        List<String> a = new ArrayList<>();
        a.add("1");
        a.add("2");
        a.add("3");

        List<String> newList = a.stream().filter(p->!StringUtils.equals("2", p)).collect(Collectors.toList());
        System.err.println(newList.toString());


//        Iterator<String> it = a.iterator();
//        while (it.hasNext()) {
//            String temp = it.next();
//            System.err.println("temp: " + temp);
//            if ("1".equals(temp)) {
//                a.remove(temp);
//            }
//        }
    }

    @Test
    public void testInfo(){
        String infoPath = "中国/山东省";
        if(!infoPath.startsWith("/")){
            log.info("HDFS地址参数不准确，应该修改成:/path1/path2");
            return ;
        }

        String[] pathNameList = infoPath.split("\\/");
        String prefix = "/";
        if(ArrayUtils.isNotEmpty(pathNameList)){

            List<String> path = Arrays.asList(pathNameList).stream().filter(p->(!p.isEmpty())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(path)) {
                prefix += path.get(0);
            }
        }
        log.info("路径前缀 {}",prefix);

    }


}
