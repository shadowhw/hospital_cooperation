package com.hl.hos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public interface MailService {


    public void sendSimpleMail(String from,String to,String cc,String subject,String content);
    public void sendMineEmail(String from,String to ,String cc, String subject ,String content);
}
