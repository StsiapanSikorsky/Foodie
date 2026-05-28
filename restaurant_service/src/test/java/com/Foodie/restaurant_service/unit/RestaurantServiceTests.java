package com.Foodie.restaurant_service.unit;

import com.Foodie.restaurant_service.advice.exceptions.DataExistsException;
import com.Foodie.restaurant_service.advice.exceptions.IncorrectRoleException;
import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.mapper.RestaurantMapper;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.request.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.impl.RestaurantServiceImpl;
import com.Foodie.restaurant_service.utils.Utils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RestaurantServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private Utils utils;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant testRestaurant;
    private Restaurant testRestaurant2;
    private RestaurantDto testRestaurantDto;
    private RestaurantDto testRestaurantDto2;
    private RestaurantRequest testRestaurantRequest;
    private AuthenticationValidationResponse testValidationResponse;
    private UpdateRestaurantRequest testUpdateRestaurantRequest;
    private SearchRestaurantRequest testSearchRestaurantRequest;

    private String jwtToken;
    private String refreshToken;
    private HttpServletResponse response;

    private Pageable pageable;

    @BeforeEach
    void setUp(){
        jwtToken = "Bearer test.jwt.token";
        refreshToken = "tets.refresh.token";
        response = mock(HttpServletResponse.class);

        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setRestaurantName("TestRestaurantName 1");
        testRestaurant.setCity("Minsk");
        testRestaurant.setAddress("Brilevskaya 37");
        testRestaurant.setType(RestaurantType.RESTAURANT);

        testRestaurant2 = new Restaurant();
        testRestaurant2.setId(2);
        testRestaurant2.setRestaurantName("TestRestaurantName 2");
        testRestaurant2.setCity("Minsk");
        testRestaurant2.setAddress("Brilevskaya 37");
        testRestaurant2.setType(RestaurantType.RESTAURANT);

        testRestaurantDto = new RestaurantDto();
        testRestaurantDto.setId(1);
        testRestaurantDto.setRestaurantName("TestRestaurantName 1");
        testRestaurantDto.setCity("Minsk");
        testRestaurantDto.setAddress("Brilevskaya 37");
        testRestaurantDto.setType(RestaurantType.RESTAURANT);

        testRestaurantDto2 = new RestaurantDto();
        testRestaurantDto2.setId(2);
        testRestaurantDto2.setRestaurantName("TestRestaurantName 2");
        testRestaurantDto2.setCity("Minsk");
        testRestaurantDto2.setAddress("Brilevskaya 37");
        testRestaurantDto2.setType(RestaurantType.RESTAURANT);

        testRestaurantRequest = new RestaurantRequest();
        testRestaurantRequest.setRestaurantName("NewRestaurant");
        testRestaurantRequest.setCity("Minsk");
        testRestaurantRequest.setType(RestaurantType.BAR);

        testUpdateRestaurantRequest = new UpdateRestaurantRequest();
        testUpdateRestaurantRequest.setRestaurantName("UpdatedRestaurantName");

        testValidationResponse = new AuthenticationValidationResponse();
        testValidationResponse.setValid(true);
        testValidationResponse.setRoles(List.of("OWNER"));
        testValidationResponse.setUserId(1);

        testSearchRestaurantRequest = new SearchRestaurantRequest();
        testSearchRestaurantRequest.setRestaurantName("TestRestaurantName");
        testSearchRestaurantRequest.setCity("Minsk");

        pageable = PageRequest.of(0,10);
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

    @Test
    void getRestaurantById_Throw_NotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurantById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(restaurantMapper,never()).toRestaurantDto(testRestaurant);
    }

    @Test
    void addNewRestaurant_Owner_Success(){
        when(restaurantRepository.existsByRestaurantName(testRestaurantRequest.getRestaurantName())).thenReturn(false);
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.checkRole(testValidationResponse)).thenReturn(true);
        when(restaurantMapper.restaurantRequestToRestaurant(testRestaurantRequest)).thenReturn(testRestaurant);
        when(restaurantRepository.save(testRestaurant)).thenReturn(testRestaurant);
        when(restaurantMapper.toRestaurantDto(testRestaurant)).thenReturn(testRestaurantDto);

        RestaurantResponse<RestaurantDto> result = restaurantService.addNewRestaurant(testRestaurantRequest, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), testRestaurantDto.getId());
        assertEquals(result.getPayload().getRestaurantName(), testRestaurantDto.getRestaurantName());

        verify(restaurantRepository, times(1)).existsByRestaurantName(testRestaurantRequest.getRestaurantName());
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).checkRole(testValidationResponse);
        verify(restaurantMapper, times(1)).restaurantRequestToRestaurant(testRestaurantRequest);
        verify(restaurantRepository, times(1)).save(testRestaurant);
        verify(restaurantMapper, times(1)).toRestaurantDto(testRestaurant);
    }

    @Test
    void addNewRestaurant_User_Throw_IncorrectRoleException(){
        when(restaurantRepository.existsByRestaurantName(testRestaurantRequest.getRestaurantName())).thenReturn(false);
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.checkRole(testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantService.addNewRestaurant(testRestaurantRequest, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("Only owner");

        verify(restaurantRepository, times(1)).existsByRestaurantName(testRestaurantRequest.getRestaurantName());
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).checkRole(testValidationResponse);
        verify(restaurantMapper, never()).restaurantRequestToRestaurant(testRestaurantRequest);
        verify(restaurantRepository, never()).save(testRestaurant);
        verify(restaurantMapper, never()).toRestaurantDto(testRestaurant);
    }

    @Test
    void addNewRestaurant_Owner_Throw_DataExistsException(){
        when(restaurantRepository.existsByRestaurantName(testRestaurantRequest.getRestaurantName())).thenReturn(true);

        assertThatThrownBy(() -> restaurantService.addNewRestaurant(testRestaurantRequest, jwtToken, refreshToken, response))
                .isInstanceOf(DataExistsException.class)
                .hasMessageContaining("was created before");

        verify(restaurantRepository, times(1)).existsByRestaurantName(testRestaurantRequest.getRestaurantName());
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).checkRole(testValidationResponse);
        verify(restaurantMapper, never()).restaurantRequestToRestaurant(testRestaurantRequest);
        verify(restaurantRepository, never()).save(testRestaurant);
        verify(restaurantMapper, never()).toRestaurantDto(testRestaurant);
    }

    @Test
    void updateRestaurant_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantMapper.updateRestaurantRequestToRestaurant(testRestaurant ,testUpdateRestaurantRequest)).thenReturn(testRestaurant);
        when(restaurantRepository.save(testRestaurant)).thenReturn(testRestaurant);
        when(restaurantMapper.toRestaurantDto(testRestaurant)).thenReturn(testRestaurantDto);

        RestaurantResponse<RestaurantDto> result = restaurantService.updateRestaurant(1, testUpdateRestaurantRequest, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), testRestaurantDto.getId());
        assertEquals(result.getPayload().getRestaurantName(), testRestaurantDto.getRestaurantName());

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantMapper, times(1)).updateRestaurantRequestToRestaurant(testRestaurant ,testUpdateRestaurantRequest);
        verify(restaurantRepository, times(1)).save(testRestaurant);
        verify(restaurantMapper, times(1)).toRestaurantDto(testRestaurant);
    }

    @Test
    void updateRestaurant_Throw_NotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.updateRestaurant(999, testUpdateRestaurantRequest, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantMapper, never()).updateRestaurantRequestToRestaurant(testRestaurant ,testUpdateRestaurantRequest);
        verify(restaurantRepository, never()).save(testRestaurant);
        verify(restaurantMapper, never()).toRestaurantDto(testRestaurant);
    }

    @Test
    void updateRestaurant_NotOwner_Throw_IncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantService.updateRestaurant(1, testUpdateRestaurantRequest, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("You don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantMapper, never()).updateRestaurantRequestToRestaurant(testRestaurant ,testUpdateRestaurantRequest);
        verify(restaurantRepository, never()).save(testRestaurant);
        verify(restaurantMapper, never()).toRestaurantDto(testRestaurant);
    }

    @Test
    void softDeleteRestaurant_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantRepository.save(testRestaurant)).thenReturn(testRestaurant);

        restaurantService.softDeleteRestaurant(1, jwtToken, refreshToken, response);

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void softDeleteRestaurant_Throw_NotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.softDeleteRestaurant(999, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantRepository, never()).save(testRestaurant);
    }

    @Test
    void softDeleteRestaurant_NotOwner_Throw_IncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantService.softDeleteRestaurant(1, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("You don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantRepository, never()).save(testRestaurant);
    }

    @Test
    void getAllRestaurants_Success_returnNotEmptyPage(){
        List<Restaurant> restaurants = List.of(testRestaurant, testRestaurant2);
        Page<Restaurant> restaurantsPage = new PageImpl<>(restaurants, pageable, restaurants.size());

        when(restaurantRepository.findAll(pageable)).thenReturn(restaurantsPage);
        when(restaurantMapper.toRestaurantDto(testRestaurant)).thenReturn(testRestaurantDto);
        when(restaurantMapper.toRestaurantDto(testRestaurant2)).thenReturn(testRestaurantDto2);

        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.getAllRestaurants(pageable);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        PaginationResponse<RestaurantDto> paginationResponse = result.getPayload();
        List<RestaurantDto> content = paginationResponse.getContent();

        assertEquals(2, content.size());
        assertEquals(testRestaurantDto.getId(), content.get(0).getId());
        assertEquals(testRestaurantDto.getRestaurantName(), content.get(0).getRestaurantName());
        assertEquals(testRestaurantDto2.getId(), content.get(1).getId());
        assertEquals(testRestaurantDto2.getRestaurantName(), content.get(1).getRestaurantName());

        verify(restaurantRepository, times(1)).findAll(pageable);
        verify(restaurantMapper, times(1)).toRestaurantDto(testRestaurant);
        verify(restaurantMapper, times(1)).toRestaurantDto(testRestaurant2);
    }

    @Test
    void getAllRestaurants_Success_returnEmptyPage(){
        List<Restaurant> restaurants = List.of();
        Page<Restaurant> restaurantsPage = new PageImpl<>(restaurants, pageable, 0);

        when(restaurantRepository.findAll(pageable)).thenReturn(restaurantsPage);

        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.getAllRestaurants(pageable);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        PaginationResponse<RestaurantDto> paginationResponse = result.getPayload();
        List<RestaurantDto> content = paginationResponse.getContent();

        assertTrue(content.isEmpty());

        verify(restaurantRepository, times(1)).findAll(pageable);
        verify(restaurantMapper, never()).toRestaurantDto(any());
    }

    @Test
    void searchRestaurants_Success_returnNotEmptyPage(){
        List<Restaurant> restaurants = List.of(testRestaurant, testRestaurant2);
        Page<Restaurant> restaurantsPage = new PageImpl<>(restaurants, pageable, restaurants.size());

        when(restaurantRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(restaurantsPage);
        when(restaurantMapper.toRestaurantDto(testRestaurant)).thenReturn(testRestaurantDto);
        when(restaurantMapper.toRestaurantDto(testRestaurant2)).thenReturn(testRestaurantDto2);

        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.searchRestaurants(testSearchRestaurantRequest, pageable);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        PaginationResponse<RestaurantDto> paginationResponse = result.getPayload();
        List<RestaurantDto> content = paginationResponse.getContent();

        assertEquals(2, content.size());
        assertEquals(testRestaurantDto.getId(), content.get(0).getId());
        assertEquals(testRestaurantDto.getRestaurantName(), content.get(0).getRestaurantName());
        assertEquals(testRestaurantDto2.getId(), content.get(1).getId());
        assertEquals(testRestaurantDto2.getRestaurantName(), content.get(1).getRestaurantName());

        verify(restaurantRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(restaurantMapper, times(1)).toRestaurantDto(testRestaurant);
        verify(restaurantMapper, times(1)).toRestaurantDto(testRestaurant2);
    }

    @Test
    void searchRestaurants_Success_returnEmptyPage(){
        List<Restaurant> restaurants = List.of();
        Page<Restaurant> restaurantsPage = new PageImpl<>(restaurants, pageable, 0);

        when(restaurantRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(restaurantsPage);

        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.searchRestaurants(testSearchRestaurantRequest, pageable);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        PaginationResponse<RestaurantDto> paginationResponse = result.getPayload();
        List<RestaurantDto> content = paginationResponse.getContent();

        assertTrue(content.isEmpty());

        verify(restaurantRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(restaurantMapper, never()).toRestaurantDto(any());
    }
}
