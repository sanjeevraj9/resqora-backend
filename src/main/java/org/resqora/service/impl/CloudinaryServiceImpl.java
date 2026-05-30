package org.resqora.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;

import org.resqora.entity.MechanicProfile;
import org.resqora.entity.User;

import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.UserRepository;

import org.resqora.service.CloudinaryService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl
        implements CloudinaryService {

    private final Cloudinary cloudinary;

    private final UserRepository userRepository;

    private final MechanicProfileRepository
            mechanicProfileRepository;

    @Override
    public String uploadProfilePhoto(

            MultipartFile file,

            String email

    ) throws Exception {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        MechanicProfile profile =
                mechanicProfileRepository
                        .findByUser(user)
                        .orElseThrow();

        Map uploadResult =
                cloudinary.uploader().upload(

                        file.getBytes(),

                        ObjectUtils.emptyMap()
                );

        String imageUrl =
                uploadResult
                        .get("secure_url")
                        .toString();

        profile.setProfileImageUrl(
                imageUrl
        );

        mechanicProfileRepository
                .save(profile);

        return imageUrl;
    }

    @Override
    public String uploadShopPhoto(

            MultipartFile file,

            String email

    ) throws Exception {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        MechanicProfile profile =
                mechanicProfileRepository
                        .findByUser(user)
                        .orElseThrow();

        Map uploadResult =
                cloudinary.uploader().upload(

                        file.getBytes(),

                        ObjectUtils.emptyMap()
                );

        String imageUrl =
                uploadResult
                        .get("secure_url")
                        .toString();

        profile.setShopImageUrl(
                imageUrl
        );

        mechanicProfileRepository
                .save(profile);

        return imageUrl;
    }
}