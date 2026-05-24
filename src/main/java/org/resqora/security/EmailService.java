package org.resqora.security;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    public void sendVerificationEmail(String toEmail,String token){
        String link="https://resqora-api.onrender.com/api/auth/verify?token="+token;

        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ResQora - Verify your Email");
        message.setText(
                "Hello! Welcome to ResQora \uD83D\uDE97\\n\\n"+
                "Please verify your email by clicking the link below:\n\n"+
                link +"\n\n+"+
                        "This link is valid for 24 hours. \n\n"+
                        "Team ResQora"
        );
        mailSender.send(message);
    }
}
