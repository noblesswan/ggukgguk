package com.ggukgguk.api.common.service;

public interface EmailService {

	public boolean sendEmail(String sendTo, String subject, String content) throws Exception;
}
