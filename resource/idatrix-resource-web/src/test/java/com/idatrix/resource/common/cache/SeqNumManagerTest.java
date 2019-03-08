package com.idatrix.resource.common.cache;

import com.idatrix.resource.datareport.dao.DataUploadDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Robin Wing on 2018-6-29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class SeqNumManagerTest {

    @Autowired
    private DataUploadDAO dataUploadDAO;

    @Autowired
    private SequenceNumberManager sequenceNumberManager;

    @Test
    public void SeqNumTest(){
        Long value = null;
        try{
            value = sequenceNumberManager.getSeqNum();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.print("**************************************");
        System.out.print("redis-value: " + value);
        System.out.print("**************************************");
    }
}
