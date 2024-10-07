package com.example.emailchecker.controller;

import com.example.emailchecker.model.User;
import com.example.emailchecker.service.rabbitmq.EmailProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/email")
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailController {
    final EmailProducer emailProducer;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody User user) {
        String  result = emailProducer.sendEmailMessage(user.getEmail(), user.getMessage());
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cavab alınmadı");
        }
    }
}
