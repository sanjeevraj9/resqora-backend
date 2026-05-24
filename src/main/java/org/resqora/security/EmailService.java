package org.resqora.security;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token) {
        String link = "https://resqora-api.onrender.com/api/auth/verify?token=" + token;

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Resqora - Verify Your Email 🚗";
        Content content = new Content("text/plain",
                "Hello! Welcome to Resqora 🚗\n\n" +
                        "Please verify your email:\n\n" +
                        link + "\n\n" +
                        "Team Resqora"
        );

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (Exception e) {
            throw new RuntimeException("Email send failed: " + e.getMessage());
        }
    }
}