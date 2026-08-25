package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.entity.User;
import com.mytoshika.ecommerceoms.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(user.getEmail());

        message.setSubject("Welcome to the Order management System");

        message.setText(
                "Hello " + user.getName() +
                        "\n\n Welcome to our order management System." +
                        "\n Your Registration has been done"
        );

        mailSender.send(message);
    }
}
