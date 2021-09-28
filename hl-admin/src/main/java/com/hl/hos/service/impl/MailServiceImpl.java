package com.hl.hos.service.impl;

import com.hl.hos.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public void sendSimpleMail(String from, String to, String cc, String subject, String content) {
        /*
         * 邮件信息
         * */
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setCc(cc);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        /*
         * 发送邮件
         * */
        javaMailSender.send(simpleMailMessage);
    }
}
