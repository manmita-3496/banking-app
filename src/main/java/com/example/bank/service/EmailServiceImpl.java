package com.example.bank.service;

import com.example.bank.dto.EmailDetails;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.util.Objects;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) throws MailException{
            SimpleMailMessage messageHelper=new SimpleMailMessage();
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(emailDetails.getRecipient());
            messageHelper.setText(emailDetails.getMessageBody());
            messageHelper.setSubject(emailDetails.getSubject());

            javaMailSender.send(messageHelper);
    }

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMessageBody());
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            FileSystemResource  file= new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            javaMailSender.send(mimeMessage);

            log.info(file.getFilename()+" has been sent to your email "+emailDetails.getRecipient());

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
