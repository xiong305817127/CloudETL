package com.idatrix.unisecurity.common.utils;

import com.idatrix.unisecurity.common.dao.MailLogMapper;
import com.idatrix.unisecurity.common.domain.MailLog;
import com.idatrix.unisecurity.properties.EmailProperties;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 邮箱工具类
 */
public class EmailUtil {

	private static EmailUtil ds;

	public static ExecutorService executor = Executors.newCachedThreadPool();

	private Logger logger = Logger.getLogger(getClass());

	private RestTemplate restTemplate;

	private EmailProperties emailProperties;

	public EmailUtil(){
		emailProperties = SpringContextUtil.getBean(EmailProperties.class);
		restTemplate = SpringContextUtil.getBean(RestTemplate.class);
	}

	public void postEmail(String email, String subject, String content, MailLogMapper mailLogMapper, MailLog mailLog) throws MessagingException{
		executor.execute(new EmailThread(email, subject, content, mailLogMapper, mailLog));
	}
	
	/**
	 * double check
	 * @return
	 */
	public static EmailUtil getInstance(){
		if(ds == null){
			synchronized (EmailUtil.class) {
				if(ds == null){
					ds = new EmailUtil();
				}
			}
		}
		return ds;
	}
	
	class EmailThread extends Thread{
		 String email;// 邮箱
		 String subject;// 表示到底是干什么：找回密码，新建租户密码通知
		 String content;// 要发送的内容
		 MailLogMapper mailLogMapper;
		 MailLog mailLog;
		 
		public EmailThread(String email, String subject, String content, MailLogMapper mailLogMapper,
				MailLog mailLog) {
			this.email = email;
			this.subject = subject;
			this.content = content;
			this.mailLogMapper = mailLogMapper;
			this.mailLog = mailLog;
		}

		@Override
		public void run() {
			String errorMessage = null;
			try {
				logger.info("EmailThread send email：" + email);

				// 只有当可以访问外网的时候才回去发送邮件
				// ResponseEntity<String> response = restTemplate.getForEntity("https://www.baidu.com/", String.class);

				Properties props = new Properties();
				props.put("mail.transport.protocol", "smtp");//邮件发送协议
				props.put("mail.smtp.host", emailProperties.getEmailServer());//SMTP邮件服务器
				props.put("mail.smtp.auth", "true");// 是否要求身份认证
				props.put("mail.smtp.port", "25");// SMTP邮件服务器默认端口
				props.put("mail.debug", emailProperties.getMailDebug());//是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息）

				// 使用环境属性和授权信息，创建邮件会话
				Session session = Session.getInstance(props, new Authenticator() {
                    // 构建授权信息，用于进行SMTP进行身份验证
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailProperties.getUser(), emailProperties.getPassword());
                    }
                });

                // 创建邮件消息
                MimeMessage message = new MimeMessage(session);
                // 设置发件人
                message.setFrom(new InternetAddress(emailProperties.getUser()));
                // 设置收件人
                InternetAddress to = new InternetAddress(email);
                message.setRecipient(Message.RecipientType.TO, to);
                // 设置邮件标题
                message.setSubject(subject);
                // 设置邮件的内容体
                message.setContent(content, "text/html;charset=UTF-8");
                // 设置发送时间
                message.setSentDate(new Date());
                // 发送邮件
                Transport.send(message);
			} catch(Exception e) {
			    //一旦发生异常就记录起来
                logger.error("EmailThread send email error :" + e.getMessage());

                mailLog.setStatus("F");
				errorMessage = e.getMessage();
				if(errorMessage.length()>100){
					errorMessage.substring(0, 100);
				}
				if(errorMessage.contains("www.baidu.com")){
					errorMessage = "当前服务器不能连接外网。发送邮件失败！！！";
				}
				mailLog.setMsg(errorMessage);
				mailLogMapper.update(mailLog);
			}
		}
	}

}
