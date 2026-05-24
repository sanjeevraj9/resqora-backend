package org.resqora.service;

public interface EmergencyService {

    void sendEmergencyAlert(
            String phone,
            String userName,
            double lat,
            double lng
    );
}