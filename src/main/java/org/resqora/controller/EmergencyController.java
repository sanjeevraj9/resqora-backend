package org.resqora.controller;


import lombok.RequiredArgsConstructor;
import org.resqora.entity.User;
import org.resqora.service.EmergencyService;
import org.resqora.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final UserService userService;

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerEmergency(@RequestParam double latitude,
                                                   @RequestParam double longitude,
                                                   Authentication authentication){
        User user=userService.findByEmail(
                authentication.getName()
        );
        if(user.getEmergencyContactPhone()==null ||
        user.getEmergencyContactPhone().isBlank()){
            return ResponseEntity.badRequest()
                    .body("Emergency contact missing");
        }
        emergencyService.sendEmergencyAlert(
                user.getEmergencyContactPhone(),
                user.getName(),
                latitude,
                longitude
        );
        return ResponseEntity.ok(
                "Emergency alert sent"
        );
    }
}
