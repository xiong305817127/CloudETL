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
 * @ClassName OracleViewServiceTest
 * @Description
 * @Author ouyang
 * @Date
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OracleViewServiceTest {

    @Autowired
    @Qualifier("oracleViewService")
    private ViewService viewService;

    @Test
    public void addView() {
        DBViewVO view = new DBViewVO();
        view.setName("view_cs18");
        view.setIdentification("view_测试18");
        view.setThemeId(1l);
        view.setPublicStatus(0);
        view.setSchemaId(43l);
        view.setDatabaseType(2);
        view.setResourceType(2);

        ViewDetail viewDetail = new ViewDetail();
        viewDetail.setViewSql("select * from \"table_51\" where \"id\" >2");

        view.setViewDetail(viewDetail);

        viewService.add(view);
    }

    @Test
    public void updateView() {
        Long viewId = 262l;
        Long detailId = 33l;
        DBViewVO view = new DBViewVO();
        view.setId(viewId);
        view.setName("view_cs9");
        view.setIdentification("view_测试9_update");
        view.setThemeId(1l);
        view.setPublicStatus(0);
        view.setSchemaId(43l);
        view.setStatus(1);
        view.setDatabaseType(2);
        view.setResourceType(2);

        ViewDetail viewDetail = new ViewDetail();
        viewDetail.setId(detailId);
        viewDetail.setViewSql("select * from \"test3\" where \"id\" >1");

        view.setViewDetail(viewDetail);

        viewService.update(view);
    }

    @Test
    public void test1() {
        String str = "123";
        str += "1";
        System.out.println(str);
    }

}