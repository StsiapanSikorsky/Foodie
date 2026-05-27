package com.Foodie.restaurant_service.service.impl;

import com.Foodie.restaurant_service.advice.exceptions.IncorrectRoleException;
import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.advice.exceptions.NullExtensionException;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.RestaurantImageService;
import com.Foodie.restaurant_service.service.S3.S3Service;
import com.Foodie.restaurant_service.utils.ErrorMessage;
import com.Foodie.restaurant_service.utils.Utils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantImageServiceImpl implements RestaurantImageService {

    private final RestaurantRepository restaurantRepository;
    private final Utils utils;
    private final S3Service s3Service;

    @Override
    public RestaurantResponse<List<String>> uploadRestaurantImage(
            @NotNull Integer restaurantId,
            @NotNull List<MultipartFile> files,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);
        if (!utils.isOwnerOrAdmin(restaurant, validationResponse)) {
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        List<String> uploadUrlsToS3 = uploadFilesToS3(restaurantId, files);

        List<String> existingUrls = restaurant.getImageUrls();
        if(existingUrls == null){
            existingUrls = new ArrayList<>();
        }
        existingUrls.addAll(uploadUrlsToS3);
        restaurant.setImageUrls(existingUrls);
        restaurantRepository.save(restaurant);

        return RestaurantResponse.createSuccessful(uploadUrlsToS3);
    }

    @Override
    public void deleteImage(
            @NotNull Integer restaurantId,
            @NotNull String imageUrl,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);
        if (!utils.isOwnerOrAdmin(restaurant, validationResponse)) {
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        List<String> restaurantImageUrls = restaurant.getImageUrls();
        if(restaurantImageUrls.contains(imageUrl)){
            s3Service.deleteFile(imageUrl);
            restaurantImageUrls.remove(imageUrl);
            restaurant.setImageUrls(restaurantImageUrls);
            restaurantRepository.save(restaurant);
        }
        else {
            throw new NotFoundException(ErrorMessage.IMAGE_URL_NOT_FOUND.getMessage(restaurantImageUrls ,restaurantId));
        }
    }

    private List<String> uploadFilesToS3(
            Integer restaurantId,
            List<MultipartFile> files
    ){
        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {

                String fileName = file.getOriginalFilename();

                String extension = "";
                if (fileName != null && fileName.contains(".")) {
                    extension = fileName.substring(fileName.lastIndexOf("."));
                } else {
                    throw new NullExtensionException(ErrorMessage.NOT_FOUND_EXTENCION.getMessage());
                }

                String fileNameInBucket = UUID.randomUUID().toString() + extension;
                String pathToFileInBucket = s3Service.uploadFile(file, "restaurant/" + restaurantId, fileNameInBucket);

                uploadedUrls.add(pathToFileInBucket);
            }
        }
        catch(NullExtensionException e){
            throw new NullExtensionException(ErrorMessage.FILE_DONT_DOWNLOAD.getMessage(e.getMessage()));
        }

        return uploadedUrls;
    }
}
