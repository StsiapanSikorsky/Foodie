package com.Foodie.restaurant_service.unit;

import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.mapper.RestaurantMapper;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.service.impl.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RestaurantServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant testRestaurant;
    private RestaurantDto testRestaurantDto;

    @BeforeEach
    void setUp(){
        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setRestaurantName("TestName");
        testRestaurant.setCity("Minsk");
        testRestaurant.setAddress("Brilevskaya 37");
        testRestaurant.setType(RestaurantType.RESTAURANT);

        testRestaurantDto = new RestaurantDto();
        testRestaurantDto.setId(1);
        testRestaurantDto.setRestaurantName("TestName");
        testRestaurantDto.setCity("Minsk");
        testRestaurantDto.setAddress("Brilevskaya 37");
        testRestaurantDto.setType(RestaurantType.RESTAURANT);
    }

    @Test
    void getRestaurantById_Success_ReturnTestRestaurantDto(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantMapper.toRestaurantDto(testRestaurant)).thenReturn(testRestaurantDto);

        RestaurantResponse<RestaurantDto> result = restaurantService.getRestaurantById(1);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), testRestaurantDto.getId());

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantMapper,times(1)).toRestaurantDto(testRestaurant);
    }
}
