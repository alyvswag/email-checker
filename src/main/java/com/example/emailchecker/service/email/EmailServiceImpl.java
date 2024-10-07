package com.example.emailchecker.service.email;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {
    final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;

    @Override
    public synchronized String sendMailAndCheckEmail(String email, String text) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("support@domain.com");
        message.setTo(email);
        message.setText("Special message: " + text);
        message.setSubject("Check your Email");
        mailSender.send(message);
        Thread.sleep(5000);//5 saniye gozle
        return readInboxEmail(email);
    }

    private String readInboxEmail(String email) throws Exception {
        String host = "imap.gmail.com";
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        Folder inbox = null;
        Store store = null;
        try {
            Session session = Session.getInstance(properties);
            store = session.getStore("imaps");
            store.connect(host, username, password);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Message[] messages = inbox.getMessages();
            if (messages.length > 0) {
                Message lastMessage = messages[messages.length - 1];
                String content = getContent(lastMessage);
                if (content.contains("Adres bulunamadı")) {
                    Pattern pattern = Pattern.compile("\\S+@gmail\\.com");
                    Matcher matcher = pattern.matcher(content);
                    while (matcher.find()) {
                        // System.out.println("Gmail adresi: " + matcher.group());//test ucun
                        if (matcher.group().equals(email)) {
                            lastMessage.setFlag(Flags.Flag.DELETED, true);
                            return email + " adressi movcud deyil";
                        }
                    }
                } else {
                    return "Mesaj tapilmadi.";
                }
            } else {
                return email + " addresi movcuddur";
            }
            return email + " addresi movcuddur";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inbox.close(false);
            store.close();
        }
        return null;
    }

    private String getContent(Message message) throws IOException, MessagingException {
        StringBuilder content = new StringBuilder();

        if (message.isMimeType("text/plain")) {
            content.append(message.getContent().toString());
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                content.append(getBodyPartContent(mimeMultipart.getBodyPart(i)));
            }
        }
        return content.toString();
    }

    private String getBodyPartContent(BodyPart bodyPart) throws IOException, MessagingException {
        if (bodyPart.isMimeType("text/plain")) {
            return bodyPart.getContent().toString();
        } else if (bodyPart.isMimeType("text/html")) {
            return bodyPart.getContent().toString();
        } else if (bodyPart.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) bodyPart.getContent();
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                content.append(getBodyPartContent(mimeMultipart.getBodyPart(i)));
            }
            return content.toString();
        }
        return "";
    }

}
