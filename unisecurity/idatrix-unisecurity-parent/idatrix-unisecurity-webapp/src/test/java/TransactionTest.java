import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.auditLog.service.AuditLogService;
import com.idatrix.unisecurity.common.domain.AuditLog;
import com.idatrix.unisecurity.common.domain.URole;
import com.idatrix.unisecurity.permission.service.RoleService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName TransactionTest
 * @Description 测试 xml 配置事物是否生效了
 * @Author ouyang
 * @Date 2018/8/29 13:43
 * @Version 1.0
 **/
public class TransactionTest {

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");

    @Test
    public void findPage(){
        AuditLogService service = applicationContext.getBean(AuditLogService.class);
        PageInfo<AuditLog> pageInfo = service.findPage(1, 10);
        System.out.println(pageInfo);
    }

    @Test
    public void insert(){
        AuditLogService service = applicationContext.getBean(AuditLogService.class);
        AuditLog auditLog = new AuditLog();
        auditLog.setServer("oyr test");
        auditLog.setResource("/login");
        auditLog.setMethodType("GET");
        auditLog.setClientIp("10.0.0.117");
        auditLog.setResult("cg");
        auditLog.setVisitTime(new Date());
        service.insert(auditLog);
    }

    @Test
    public void roleInsert(){
        RoleService roleService = applicationContext.getBean(RoleService.class);
        URole role = new URole();
        role.setRenterId(100);
        role.setCreateTime(new Date());
        role.setName("oyr test1");
        roleService.insert(role);
    }

    @Test
    public void userUpdate(){
        /*UUserService userServicer = applicationContext.getBean(UUserService.class);
        UUser user = new UUser();
        user.setId(996l);
        user.setAge(1000);
        user.setEmail("14745625890@163.com");
        userServicer.updateByPrimaryKeySelective(user);*/


        Date now = new Date();
        Date now2 = new Date(now.getTime() + 30 * 60 * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(now2));
    }


}
