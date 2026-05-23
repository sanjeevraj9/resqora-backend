package org.resqora.service.impl;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.response.AcceptRequestNotification;
import org.resqora.dto.response.LiveLocationNotification;
import org.resqora.dto.response.RequestClosedNotification;
import org.resqora.dto.response.RequestNotificationResponse;
import org.resqora.dto.response.RequestStatusNotification;
import org.resqora.entity.User;
import org.resqora.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyMechanic(
            User mechanic,
            RequestNotificationResponse request
    ) {
        messagingTemplate.convertAndSend(
                "/topic/mechanic-requests",
                request
        );
    }

    @Override
    public void notifyUser(
            User user,
            AcceptRequestNotification notification
    ) {

        

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/topic/request-accepted",
                notification
        );
    }

    @Override
    public void notifyRequestClosed(
            RequestClosedNotification notification
    ) {
        messagingTemplate.convertAndSend(
                "/topic/request-closed",
                notification
        );
    }

    @Override
    public void notifyStatusUpdate(
            User user,
            RequestStatusNotification notification
    ) {
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/topic/request-status",
                notification
        );
    }

    @Override
    public void notifyLiveLocation(
            User user,
            LiveLocationNotification notification
    ) {
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/topic/live-location",
                notification
        );
    }
}