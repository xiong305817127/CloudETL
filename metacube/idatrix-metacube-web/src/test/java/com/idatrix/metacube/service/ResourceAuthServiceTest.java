package com.idatrix.metacube.service;

import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;
import com.ys.idatrix.metacube.metamanage.service.ResourceAuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceAuthServiceTest {

    @Autowired
    private ResourceAuthService resourceAuthService;

    @Test
    public void findAll() throws Exception {
        List<ResourceAuth> list = resourceAuthService.findAll();
        System.out.println(list);
    }

}