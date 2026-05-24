package org.resqora.service.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.resqora.service.EmergencyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class EmergencyServiceImpl
        implements EmergencyService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhone;

    @Override
    public void sendEmergencyAlert(
            String phone,
            String userName,
            double lat,
            double lng
    ) {

        Twilio.init(accountSid, authToken);

        String mapLink =
                "https://maps.google.com/?q="
                        + lat + "," + lng;

        String message =
                "🚨 Emergency Alert from Resqora. "
                        + userName
                        + " may be in an accident. "
                        + "Live location: "
                        + mapLink;

        Message.creator(
                new PhoneNumber(phone),
                new PhoneNumber(twilioPhone),
                message
        ).create();

        Call.creator(
                new PhoneNumber(phone),
                new PhoneNumber(twilioPhone),
                URI.create(
                        "http://demo.twilio.com/docs/voice.xml"
                )
        ).create();
    }
}