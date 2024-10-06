package com.ZigzagPayment.payment.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendOtpMail(String email, String username, String otp) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("otp", otp);
            String htmlContent = templateEngine.process("otp-email-template.html", context);

            helper.setFrom("shubhamjagdalerxl@gmail.com");
            helper.setTo(email);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending OTP email: {}", e.getMessage(), e);
            throw e;
        }
    }
}
