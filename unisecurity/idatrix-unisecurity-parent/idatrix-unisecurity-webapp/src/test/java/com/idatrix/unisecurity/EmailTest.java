package com.idatrix.unisecurity;

import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName EmailTest
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/27 11:57
 * @Version 1.0
 **/
public class EmailTest {

    @Test
    public void send_163email(){
        try {
            String server_name = "smtp.163.com";
            String user_name = "service@gdbigdata.com";
            String user_password = "MTIzNDU2Nzg=";

            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");//邮件发送协议
            props.put("mail.smtp.host", server_name);//SMTP邮件服务器
            props.put("mail.smtp.auth", "true");//是否要求身份认证
            props.put("mail.smtp.port", "25");//SMTP邮件服务器默认端口
            props.put("mail.debug", "true");//是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息）

            // 发件人的账号
            props.put("mail.user", user_name);
            //发件人的密码
            props.put("mail.password", "MTIzNDU2Nzg");

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication("service@gdbigdata.com", "MTIzNDU2Nzg=");
                }
            };

            // 使用环境属性和授权信息，创建邮件会话
            Session session = Session.getInstance(props, authenticator);

            // 创建邮件消息
            MimeMessage message = new MimeMessage(session);
            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress("service@gdbigdata.com");
            message.setFrom(form);

            // 设置收件人
            InternetAddress to = new InternetAddress("oyr999624@163.com");
            message.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件标题
            message.setSubject("邮件标题_test");
            // 设置邮件的内容体
            message.setContent("内容_test", "text/html;charset=UTF-8");
            //设置发送时间
            message.setSentDate(new Date());
            // 发送邮件
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 发送成功！！！
    @Test
    public void sendMailTest() throws MessagingException {
        String username = "oyr999624@163.com";
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "SMTP");
        props.setProperty("mail.host", "smtp.163.com");
        props.setProperty("mail.smtp.auth", "true");// 指定验证为true是否需要身份验证

        // 创建验证器
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                // 密码验证 oyr999624@163.com
                return new PasswordAuthentication(username, "o15216170956");
            }
        };

        Session session = Session.getInstance(props, auth);
        // 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username)); // 设置发送者
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("1364782109@qq.com")); // 设置发送方式与接收者

        // 设置邮件标题
        message.setSubject("邮件标题_test");
        // 设置邮件的内容体
        message.setContent("内容_test", "text/html;charset=UTF-8");
        //设置发送时间
        message.setSentDate(new Date());

        // 3.创建 Transport用于将邮件发送
        Transport.send(message);
    }
}

