package com.example.emailchecker.service.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String sendEmailMessage(String email, String text) {
        rabbitTemplate.setReplyTimeout(60000);
        Map<String, String> message = new HashMap<>();
        message.put("email", email);
        message.put("text", text);
        Object response = rabbitTemplate.convertSendAndReceive("emailTopicExchange", "email.key", message);

        if (response != null) {
            return "Cavab: " + response.toString();
        } else {
            return "Heç bir cavab almadı.";
        }
    }


}

