package com.example.emailchecker.service.email;

public interface EmailService {
    String sendMailAndCheckEmail(String email,String text) throws Exception;

}
