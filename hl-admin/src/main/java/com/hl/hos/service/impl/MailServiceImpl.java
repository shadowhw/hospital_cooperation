package com.hl.hos.service.impl;

import com.hl.hos.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    JavaMailSender javaMailSender;

    /**
     * 发送简单邮件
     * @param from 发送Client
     * @param to 到哪去
     * @param cc
     * @param subject 主题
     * @param content 内容
     */
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

    /**
     * 支持发送Html
     * @param from
     * @param to
     * @param cc
     * @param subject
     * @param content
     */
    @Override
    public void sendMineEmail(String from, String to, String cc, String subject, String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            //true为支持Html
            MimeMessageHelper mimeMessageHelper  = new MimeMessageHelper(mimeMessage,true,"UTF-8");
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setTo(to);
            mimeMessage.setFrom(from);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(content, String.valueOf(true));

            javaMailSender.send(mimeMessage);


        }catch (Exception e){
            System.out.println(e);
        }
    }

}
