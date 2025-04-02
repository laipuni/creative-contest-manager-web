package com.example.cpsplatform.auth.email;

import com.example.cpsplatform.exception.MailSendingFailedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;
    public void sendMail(final String to, final String title, final String content){
        MimeMessage message = generateMessage(to, title, content);
        mailSender.send(message);
        log.info("Success! send mail to {}", to);
    }

    private MimeMessage generateMessage(final String to, final String title, final String content){
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.addRecipients(Message.RecipientType.TO,to);
            message.setSubject(title);
            message.setFrom(new InternetAddress(username));
            message.setText(content,"UTF-8","html");
        } catch (MessagingException e) {
            log.warn("fail send mail to {}, cause = {}", to, e.getMessage());
            throw new MailSendingFailedException("메일 전송 중 오류가 발생했습니다.", e);
        }
        return message;
    }

}
