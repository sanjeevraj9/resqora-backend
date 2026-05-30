package org.resqora.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadProfilePhoto(
            MultipartFile file,
            String email
    ) throws Exception;

    String uploadShopPhoto(MultipartFile file,
                           String email
    ) throws Exception;
}
