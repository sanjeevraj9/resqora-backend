package org.resqora.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.resqora.entity.User;
import org.resqora.enums.Role;
import org.resqora.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtService jwtService;
    @Value(("${FRONTEND_URL:http://localhost:4200}"))
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(

            HttpServletRequest request,

            HttpServletResponse response,

            Authentication authentication

    ) throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User)
                        authentication.getPrincipal();

        String email =
                oauthUser.getAttribute("email");

        String name =
                oauthUser.getAttribute("name");

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(() -> {

                            User newUser =
                                    new User();

                            newUser.setName(name);

                            newUser.setEmail(email);

                            newUser.setPassword(
                                    UUID.randomUUID().toString()
                            );

                            newUser.setPhone(null);


                            newUser.setRole(
                                    Role.USER
                            );

                            return userRepository
                                    .save(newUser);
                        });

        String token =
                jwtService.generateToken(

                        org.springframework
                                .security
                                .core
                                .userdetails
                                .User

                                .withUsername(
                                        user.getEmail()
                                )

                                .password(
                                        user.getPassword()
                                )

                                .authorities(
                                        "ROLE_" +
                                                user.getRole().name()
                                )

                                .build()
                );

        response.sendRedirect(

                frontendUrl + "/oauth-success"

                        + "?token=" + token

                        + "&role=" +
                        user.getRole().name()

                        + "&phone=" +
                        (
                                user.getPhone() != null
                                        ? user.getPhone()
                                        : ""
                        )

                        + "&name=" +
                        java.net.URLEncoder.encode(
                                user.getName(),
                                java.nio.charset.StandardCharsets.UTF_8
                        )
                        + "&id=" + user.getId()
        );
    }
}