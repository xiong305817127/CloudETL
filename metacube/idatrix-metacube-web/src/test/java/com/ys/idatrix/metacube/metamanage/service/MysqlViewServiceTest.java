package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.ViewDetail;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName MysqlViewServiceTest
 * @Description MysqlViewService 服务测试类
 * @Author ouyang
 * @Date
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlViewServiceTest {

    @Autowired
    @Qualifier("mysqlViewService")
    private ViewService mysqlViewService;

    @Test
    public void add() {
        DBViewVO view = new DBViewVO();
        view.setName("view_cs22");
        view.setIdentification("view_测试22");
        view.setThemeId(1l);
        view.setPublicStatus(0);
        view.setSchemaId(33l);
        view.setDatabaseType(1);
        view.setResourceType(2);

        ViewDetail viewDetail = new ViewDetail();
        viewDetail.setViewSql("select * from table_11 where id >3");

        view.setViewDetail(viewDetail);

        mysqlViewService.add(view);
    }

    @Test
    public void update() {
        DBViewVO view = new DBViewVO();
        view.setId(188l);
        view.setName("view_cs9");
        view.setIdentification("view_测试9_update");
        view.setThemeId(1l);
        view.setPublicStatus(0);
        view.setSchemaId(33l);
        view.setStatus(1);

        ViewDetail viewDetail = new ViewDetail();
        viewDetail.setId(20l);
        viewDetail.setViewSql("select * from table_16 where id >2");

        view.setViewDetail(viewDetail);

        mysqlViewService.update(view);
    }

}