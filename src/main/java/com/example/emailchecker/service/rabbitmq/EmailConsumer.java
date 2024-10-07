package com.example.emailchecker.service.rabbitmq;

import com.example.emailchecker.service.email.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailConsumer {

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "emailQueue")
    public String receiveMessage(Map<String, String> message) throws Exception {
        String email = message.get("email");
        String text = message.get("text");
        return emailService.sendMailAndCheckEmail(email, text);
    }
}

