package com.ggukgguk.api.common.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	
	private Logger log = LogManager.getLogger("base");
	
	@Value("${mail.mailFrom}")
	private String MAIL_FROM;
	
	
	@Value("${mail.mailFromName}")
	private String MAIL_FROM_NAME;

	@Value("${mail.smtpUsername}")
	private String SMTP_USERNAME;
	
	@Value("${mail.smtpPassword}")
	private String SMTP_PW;
	
	@Value("${mail.smtpHost}")
	private String SMTP_HOST;
	
	@Value("${mail.smtpPort}")
	private String SMTP_PORT;
	
	@Value("${mail.configSet}")
	private String CONFIGSET;

	@Override
	public boolean sendEmail(String sendTo, String subject, String content) throws Exception {

		String body = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>꾹꾹 안내 메일</h1>",
    	    content
    	);
		
		Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", SMTP_PORT); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");
    	
    	Session session = Session.getDefaultInstance(props);
    	
    	MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(MAIL_FROM, MAIL_FROM_NAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
        msg.setSubject(subject, "UTF-8");
        msg.setContent(body, "text/html; charset=UTF-8");
        
        msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
        
        Transport transport = session.getTransport();
		
        try
        {
            log.debug("메일 보내는 중...");
            log.debug("수신자: " + sendTo);
            log.debug("내용: " + subject);
            log.debug("내용: " + content);
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(SMTP_HOST, SMTP_USERNAME, SMTP_PW);
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log.debug("Email sent!");
            
        } catch (Exception ex) {
        	log.debug("The email was not sent.");
        	log.debug("Error message: " + ex.getMessage());
        	return false;
        } finally {
            // Close and terminate the connection.
            transport.close();
         
        }
        return true;
	}

}
