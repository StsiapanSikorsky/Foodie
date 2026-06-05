package com.Foodie.restaurant_service.unit;

import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.entity.RestaurantTable;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.repository.RestaurantTableRepository;
import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import com.Foodie.restaurant_service.service.impl.BookingIntegrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class BookingIntegrationServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @InjectMocks
    private BookingIntegrationServiceImpl bookingIntegrationService;

    private Restaurant testRestaurant;
    private RestaurantCheckResponse testRestaurantCheckResponse;
    private RestaurantTable testRestaurantTable;


    @BeforeEach
    void setUp(){
        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setOwnerId(1);
        testRestaurant.setRestaurantName("TestRestaurantName 1");
        testRestaurant.setCity("Minsk");
        testRestaurant.setAddress("Brilevskaya 37");
        testRestaurant.setType(RestaurantType.RESTAURANT);

        testRestaurantCheckResponse = new RestaurantCheckResponse();
        testRestaurantCheckResponse.setOwner(true);
        testRestaurantCheckResponse.setRestaurantId(1);
        testRestaurantCheckResponse.setNumberOfTable(1);
        testRestaurantCheckResponse.setGuests(10);

        List<RestaurantTable> tables = new ArrayList<>();

        testRestaurantTable = new RestaurantTable();
        tables.add(testRestaurantTable);
        testRestaurantTable.setRestaurant(testRestaurant);
        testRestaurantTable.setId(1L);
        testRestaurantTable.setNumberOfTable(1);
        testRestaurantTable.setCapacity(10);
        testRestaurant.setRestaurantTables(tables);

    }

    @Test
    void existRestaurantByIdAndCheckOwner_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantTableRepository.existsByRestaurantIdAndNumberOfTable(1,1)).thenReturn(true);

        RestaurantCheckResponse result = bookingIntegrationService.existRestaurantByIdAndCheckOwner(1,1,1);

        assertNotNull(result);
        assertEquals(result.getRestaurantId(), testRestaurant.getId());
        assertEquals(result.isOwner(), true);
        assertEquals(result.getGuests(), 10);

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, times(1)).existsByRestaurantIdAndNumberOfTable(1,1);
    }

    @Test
    void existRestaurantByIdAndCheckOwner_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingIntegrationService.existRestaurantByIdAndCheckOwner(999,1,1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(restaurantTableRepository, never()).existsByRestaurantIdAndNumberOfTable(1,1);
    }

    @Test
    void existRestaurantByIdAndCheckOwner_TableNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantTableRepository.existsByRestaurantIdAndNumberOfTable(1,999)).thenReturn(false);

        assertThatThrownBy(() -> bookingIntegrationService.existRestaurantByIdAndCheckOwner(1,1,999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, times(1)).existsByRestaurantIdAndNumberOfTable(1,999);
    }

    @Test
    void getRestaurantIdWhenUserIsOwner_Success(){
        when(restaurantRepository.findByOwnerId(1)).thenReturn(Optional.of(testRestaurant));

        Integer result = bookingIntegrationService.getRestaurantIdWhenUserIsOwner(1);

        assertNotNull(result);
        assertEquals(result, 1);

        verify(restaurantRepository, times(1)).findByOwnerId(1);
    }

    @Test
    void getRestaurantIdWhenUserIsOwner_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByOwnerId(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingIntegrationService.getRestaurantIdWhenUserIsOwner(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(restaurantRepository, times(1)).findByOwnerId(999);
    }

    @Test
    void checkRestaurantOwner_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));

        Boolean result = bookingIntegrationService.checkRestaurantOwner(1, 1);

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
    }

    @Test
    void checkRestaurantOwner_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingIntegrationService.checkRestaurantOwner(999, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
    }
}
