package com.ggukgguk.api;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ggukgguk.api.common.service.EmailService;
import com.ggukgguk.api.common.vo.BasicResp;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	private EmailService emailService;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		log.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@GetMapping(value = "/mailTest", produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> testMail(@RequestParam String sendTo) throws Exception {
		
		BasicResp<Object> resp = null;
		
		if (sendTo == null || sendTo.equals("")) {
			resp = new BasicResp<Object>("success", "수신자 메일이 잘못되었습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}
		
		boolean result = emailService.sendEmail(sendTo,
				"꾹꾹 메일 전송 테스트입니다.",
				"<div>이 문장이 잘 보인다면 메일 전송에 성공한 것입니다.<br>축하합니다.</div>");
		
		if (result) {
			resp = new BasicResp<Object>("success", null, null);
			return ResponseEntity.ok(resp);
		} else {
			resp = new BasicResp<Object>("success", "메일 전송에 실패했습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}
	}
	
}
