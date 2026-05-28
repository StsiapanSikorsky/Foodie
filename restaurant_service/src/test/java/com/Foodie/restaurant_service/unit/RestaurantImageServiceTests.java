package com.Foodie.restaurant_service.unit;

import com.Foodie.restaurant_service.advice.exceptions.IncorrectRoleException;
import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.advice.exceptions.NullExtensionException;
import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.S3.S3Service;
import com.Foodie.restaurant_service.service.impl.RestaurantImageServiceImpl;
import com.Foodie.restaurant_service.utils.Utils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RestaurantImageServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private Utils utils;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private RestaurantImageServiceImpl restaurantImageService;

    private Restaurant testRestaurant;
    private AuthenticationValidationResponse testValidationResponse;
    private MultipartFile mockFile;
    private List<MultipartFile> files;

    private String jwtToken;
    private String refreshToken;
    private HttpServletResponse response;

    private String expectedUrl;
    private String deletedUrl;

    @BeforeEach
    void setUp(){
        jwtToken = "Bearer test.jwt.token";
        refreshToken = "tets.refresh.token";
        response = mock(HttpServletResponse.class);

        expectedUrl = "http://localhost:9000/bucket/restaurant/1/uuid_test1.jpg";
        deletedUrl = "http://localhost:9000/bucket/restaurant/1/uuid_test2.jpg";

        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setRestaurantName("TestRestaurantName 1");
        testRestaurant.setCity("Minsk");
        testRestaurant.setAddress("Brilevskaya 37");
        testRestaurant.setType(RestaurantType.RESTAURANT);

        List<String> imageUrls = new ArrayList<>();
        imageUrls.add(deletedUrl);
        testRestaurant.setImageUrls(imageUrls);

        testValidationResponse = new AuthenticationValidationResponse();
        testValidationResponse.setValid(true);
        testValidationResponse.setRoles(List.of("OWNER"));
        testValidationResponse.setUserId(1);

        mockFile = mock(MultipartFile.class);
        files = List.of(mockFile);
    }

    @Test
    void uploadRestaurantImage_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(s3Service.uploadFile(any(MultipartFile.class), anyString(), anyString())).thenReturn(expectedUrl);


        RestaurantResponse<List<String>> result = restaurantImageService.uploadRestaurantImage(1, files, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertEquals(1, result.getPayload().size());
        assertEquals(expectedUrl, result.getPayload().get(0));

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString(), anyString());
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void uploadRestaurantImage_ThrowNullExtensionException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(mockFile.getOriginalFilename()).thenReturn("test");

        assertThatThrownBy(() -> restaurantImageService.uploadRestaurantImage(1, files, jwtToken, refreshToken, response))
                .isInstanceOf(NullExtensionException.class)
                .hasMessageContaining("File dont download");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString(), anyString());
        verify(restaurantRepository, never()).save(testRestaurant);
    }

    @Test
    void uploadRestaurantImage_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantImageService.uploadRestaurantImage(999, files, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString(), anyString());
        verify(restaurantRepository, never()).save(testRestaurant);
    }

    @Test
    void uploadRestaurantImage_NotOwner_ThrowIncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantImageService.uploadRestaurantImage(1, files, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("You don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString(), anyString());
        verify(restaurantRepository, never()).save(testRestaurant);
    }

    @Test
    void deleteImage_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantRepository.save(testRestaurant)).thenReturn(testRestaurant);

        restaurantImageService.deleteImage(1, deletedUrl, jwtToken, refreshToken, response);

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, times(1)).deleteFile(deletedUrl);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void deleteImage_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantImageService.deleteImage(999, deletedUrl, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, never()).deleteFile(deletedUrl);
        verify(restaurantRepository, never()).save(testRestaurant);
    }

    @Test
    void deleteImage_NotOwner_ThrowIncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantImageService.deleteImage(1, deletedUrl, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("You don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(s3Service, never()).deleteFile(deletedUrl);
        verify(restaurantRepository, never()).save(testRestaurant);
    }
}
