package com.ys.idatrix.metacube.dubbo.provider;

import com.ys.idatrix.metacube.api.beans.Database;
import com.ys.idatrix.metacube.api.service.MetadataDatabaseService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MetadataDatabaseServiceImplTest {

    @Autowired
    private MetadataDatabaseService databaseService;

    @Test
    public void registerOrUpdatePlatformDatabaseInfo() {
    }

    @Test
    public void listDatabase() {
        List<Database> databaseList = databaseService.listDatabase("wzl").getData();
        System.out.println(databaseList);
    }

    @Test
    public void listDatabaseWithModuleAuth() {
    }

    @Test
    public void getDatabaseById() {
    }
}