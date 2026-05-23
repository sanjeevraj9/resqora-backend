package org.resqora.service;

import org.resqora.dto.response.AcceptRequestNotification;
import org.resqora.dto.response.LiveLocationNotification;
import org.resqora.dto.response.RequestClosedNotification;
import org.resqora.dto.response.RequestNotificationResponse;
import org.resqora.dto.response.RequestStatusNotification;
import org.resqora.entity.User;

public interface NotificationService {

    void notifyMechanic(
            User mechanic,
            RequestNotificationResponse request
    );

    void notifyUser(
            User user,
            AcceptRequestNotification notification
    );

    void notifyRequestClosed(
            RequestClosedNotification notification
    );

    void notifyStatusUpdate(
            User user,
            RequestStatusNotification notification
    );

    void notifyLiveLocation(
            User user,
            LiveLocationNotification notification
    );
}