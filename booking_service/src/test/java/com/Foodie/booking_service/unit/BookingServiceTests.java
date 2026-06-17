package com.Foodie.booking_service.unit;

import com.Foodie.booking_service.advice.exception.BookingConflictException;
import com.Foodie.booking_service.advice.exception.IncorrectRoleException;
import com.Foodie.booking_service.advice.exception.NotFoundException;
import com.Foodie.booking_service.controllers.feignRestaurantService.RestaurantServiceClient;
import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.entity.Booking;
import com.Foodie.booking_service.enums.BookingStatus;
import com.Foodie.booking_service.mapper.BookingMapper;
import com.Foodie.booking_service.repository.BookingRepository;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.request.UpdateBookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.PaginationResponse;
import com.Foodie.booking_service.response.authentication.AuthenticationValidationResponse;
import com.Foodie.booking_service.response.restaurant.RestaurantCheckResponse;
import com.Foodie.booking_service.services.impl.BookingServiceImpl;
import com.Foodie.booking_service.services.impl.CacheService;
import com.Foodie.booking_service.utils.AuthenticationUtils;
import feign.FeignException;
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
import org.springframework.data.redis.core.RedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class BookingServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RestaurantServiceClient restaurantServiceClient;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private AuthenticationUtils authenticationUtils;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking testBooking;
    private Booking testBooking2;
    private BookingDto testbookingDto;
    private BookingDto testbookingDto2;
    private BookingRequest testBookingRequest;
    private BookingRequest testBookingRequestManyGuests;
    private UpdateBookingRequest testUpdateBookingRequest;
    private AuthenticationValidationResponse testValidationResponseOwner;
    private AuthenticationValidationResponse testValidationResponseUser;
    private RestaurantCheckResponse testRestaurantCheckResponse;
    private RestaurantCheckResponse testRestaurantCheckResponseNotOwner;

    private String jwtToken;
    private String refreshToken;
    private HttpServletResponse response;
    private Pageable pageable;

    @BeforeEach
    void setUp(){
        jwtToken = "Bearer test.jwt.token";
        refreshToken = "tets.refresh.token";
        response = mock(HttpServletResponse.class);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUserId(1);
        testBooking.setRestaurantId(1);
        testBooking.setTableNumber(1);
        testBooking.setGuests(1);

        testBooking2 = new Booking();
        testBooking2.setId(2L);
        testBooking2.setUserId(1);
        testBooking2.setRestaurantId(1);
        testBooking2.setTableNumber(2);
        testBooking2.setGuests(2);

        testbookingDto = new BookingDto();
        testbookingDto.setId(1L);
        testbookingDto.setUserId(1);
        testbookingDto.setRestaurantId(1);
        testbookingDto.setTableNumber(1);
        testbookingDto.setGuests(1);

        testbookingDto2 = new BookingDto();
        testbookingDto2.setId(2L);
        testbookingDto2.setUserId(1);
        testbookingDto2.setRestaurantId(1);
        testbookingDto2.setTableNumber(2);
        testbookingDto2.setGuests(2);

        testBookingRequest = new BookingRequest();
        testBookingRequest.setTableNumber(1);
        testBookingRequest.setGuests(1);
        testBookingRequest.setBookingFrom(LocalDateTime.now().plusHours(2));
        testBookingRequest.setBookingTo(LocalDateTime.now().plusHours(3));

        testBookingRequestManyGuests = new BookingRequest();
        testBookingRequestManyGuests.setTableNumber(1);
        testBookingRequestManyGuests.setGuests(20);
        testBookingRequestManyGuests.setBookingFrom(LocalDateTime.now().plusHours(2));
        testBookingRequestManyGuests.setBookingTo(LocalDateTime.now().plusHours(3));

        testUpdateBookingRequest = new UpdateBookingRequest();
        testUpdateBookingRequest.setTableNumber(2);
        testUpdateBookingRequest.setGuests(2);

        testRestaurantCheckResponse = new RestaurantCheckResponse();
        testRestaurantCheckResponse.setOwner(true);
        testRestaurantCheckResponse.setRestaurantId(1);
        testRestaurantCheckResponse.setNumberOfTable(1);
        testRestaurantCheckResponse.setGuests(10);

        testRestaurantCheckResponseNotOwner = new RestaurantCheckResponse();
        testRestaurantCheckResponseNotOwner.setOwner(false);
        testRestaurantCheckResponseNotOwner.setRestaurantId(1);
        testRestaurantCheckResponseNotOwner.setNumberOfTable(1);
        testRestaurantCheckResponseNotOwner.setGuests(10);

        testValidationResponseOwner = new AuthenticationValidationResponse();
        testValidationResponseOwner.setValid(true);
        testValidationResponseOwner.setRoles(List.of("OWNER"));
        testValidationResponseOwner.setUserId(1);

        testValidationResponseUser = new AuthenticationValidationResponse();
        testValidationResponseUser.setValid(true);
        testValidationResponseUser.setRoles(List.of("USER"));
        testValidationResponseUser.setUserId(1);

        pageable = PageRequest.of(0,10);
    }

    @Test
    void createBooking_Success(){
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(restaurantServiceClient.getRestaurantIdAndCheckOwner(1, testValidationResponseOwner.getUserId(), testBookingRequest.getTableNumber())).thenReturn(testRestaurantCheckResponse);
        when(bookingRepository.existsConflictingBooking(1, testBookingRequest.getTableNumber(), testBookingRequest.getBookingFrom(),testBookingRequest.getBookingTo())).thenReturn(false);
        when(bookingMapper.bookingRequestToBooking(1, testValidationResponseOwner.getUserId(), testBookingRequest)).thenReturn(testBooking);
        when(bookingRepository.save(testBooking)).thenReturn(testBooking);
        when(bookingMapper.toBookingDto(testBooking)).thenReturn(testbookingDto);

        BookingResponse<BookingDto> result = bookingService.createBooking(1, testBookingRequest, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), 1);
        assertEquals(result.getPayload().getRestaurantId(), testBooking.getRestaurantId());
        assertEquals(result.getPayload().getTableNumber(), testBookingRequest.getTableNumber());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, times(1)).getRestaurantIdAndCheckOwner(1, testValidationResponseOwner.getUserId(), testBookingRequest.getTableNumber());
        verify(bookingRepository, times(1)).existsConflictingBooking(1, testBookingRequest.getTableNumber(), testBookingRequest.getBookingFrom(),testBookingRequest.getBookingTo());
        verify(bookingMapper,times(1)).bookingRequestToBooking(1, testValidationResponseOwner.getUserId(), testBookingRequest);
        verify(bookingRepository,times(1)).save(testBooking);
        verify(bookingMapper, times(1)).toBookingDto(testBooking);
    }

    @Test
    void createBooking_RestaurantNotFound_ThrowNotFoundException(){
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(restaurantServiceClient.getRestaurantIdAndCheckOwner(999, testValidationResponseOwner.getUserId(), testBookingRequest.getTableNumber())).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> bookingService.createBooking(999, testBookingRequest, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class);

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, times(1)).getRestaurantIdAndCheckOwner(999, testValidationResponseOwner.getUserId(), testBookingRequest.getTableNumber());
        verify(bookingRepository, times(0)).existsConflictingBooking(1, testBookingRequest.getTableNumber(), testBookingRequest.getBookingFrom(),testBookingRequest.getBookingTo());
        verify(bookingMapper,times(0)).bookingRequestToBooking(1, testValidationResponseOwner.getUserId(), testBookingRequest);
        verify(bookingRepository,times(0)).save(testBooking);
        verify(bookingMapper, times(0)).toBookingDto(testBooking);
    }

    @Test
    void createBooking_ManyGuestsForChooseTable_ThrowBookingConflictException(){
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(restaurantServiceClient.getRestaurantIdAndCheckOwner(1, testValidationResponseOwner.getUserId(), testBookingRequestManyGuests.getTableNumber())).thenReturn(testRestaurantCheckResponse);

        assertThatThrownBy(() -> bookingService.createBooking(1, testBookingRequestManyGuests, jwtToken, refreshToken, response))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("max guests");

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, times(1)).getRestaurantIdAndCheckOwner(1, testValidationResponseOwner.getUserId(), testBookingRequestManyGuests.getTableNumber());
        verify(bookingRepository, times(0)).existsConflictingBooking(1, testBookingRequest.getTableNumber(), testBookingRequest.getBookingFrom(),testBookingRequest.getBookingTo());
        verify(bookingMapper,times(0)).bookingRequestToBooking(1, testValidationResponseOwner.getUserId(), testBookingRequest);
        verify(bookingRepository,times(0)).save(testBooking);
        verify(bookingMapper, times(0)).toBookingDto(testBooking);
    }

    @Test
    void createBooking_TimeBookingConflict_ThrowBookingConflictException(){
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(restaurantServiceClient.getRestaurantIdAndCheckOwner(1, testValidationResponseOwner.getUserId(), testBookingRequest.getTableNumber())).thenReturn(testRestaurantCheckResponse);
        when(bookingRepository.existsConflictingBooking(1, testBookingRequest.getTableNumber(), testBookingRequest.getBookingFrom(),testBookingRequest.getBookingTo())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(1, testBookingRequest, jwtToken, refreshToken, response))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("you time was busy");

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, times(1)).getRestaurantIdAndCheckOwner(1, testValidationResponseOwner.getUserId(), testBookingRequest.getTableNumber());
        verify(bookingRepository, times(1)).existsConflictingBooking(1, testBookingRequest.getTableNumber(), testBookingRequest.getBookingFrom(),testBookingRequest.getBookingTo());
        verify(bookingMapper,times(0)).bookingRequestToBooking(1, testValidationResponseOwner.getUserId(), testBookingRequest);
        verify(bookingRepository,times(0)).save(testBooking);
        verify(bookingMapper, times(0)).toBookingDto(testBooking);
    }

    @Test
    void getBookingById_SuccessFromDB(){
        when(cacheService.findById(1L)).thenReturn(Optional.empty());
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingMapper.toBookingDto(testBooking)).thenReturn(testbookingDto);

        BookingResponse<BookingDto> result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), testbookingDto.getId());

        verify(cacheService, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findById(1L);
        verify(cacheService, times(1)).saveBookingDto(1L, testBooking);
        verify(bookingMapper, times(1)).toBookingDto(testBooking);
    }

    @Test
    void getBookingById_SuccessFromCache(){
        when(cacheService.findById(1L)).thenReturn(Optional.of(testbookingDto));

        BookingResponse<BookingDto> result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), testbookingDto.getId());

        verify(cacheService, times(1)).findById(1L);
        verify(bookingRepository, never()).findById(1L);
        verify(cacheService, never()).saveBookingDto(1L, testBooking);
        verify(bookingMapper, never()).toBookingDto(testBooking);
    }

    @Test
    void getUserBookings_SuccessFromDB() {
        List<Booking> bookingList = List.of(testBooking, testBooking2);
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());

        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseUser);
        when(cacheService.findUserPaginationBookings(testValidationResponseUser.getUserId(), pageable)).thenReturn(Optional.empty());
        when(bookingRepository.findAllByUserId(testValidationResponseUser.getUserId(), pageable)).thenReturn(bookingPage);
        when(bookingMapper.toBookingDto(testBooking)).thenReturn(testbookingDto);
        when(bookingMapper.toBookingDto(testBooking2)).thenReturn(testbookingDto2);

        BookingResponse<PaginationResponse<BookingDto>> result = bookingService.getUserBookings(pageable, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertEquals(2, result.getPayload().getContent().size());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(cacheService, times(1)).findUserPaginationBookings(testValidationResponseUser.getUserId(), pageable);
        verify(bookingRepository, times(1)).findAllByUserId(testValidationResponseUser.getUserId(), pageable);
        verify(cacheService, times(1)).savePaginationBookingDto(anyString(), any(PaginationResponse.class));
        verify(bookingMapper, times(2)).toBookingDto(any(Booking.class));
    }

    @Test
    void getUserBookings_SuccessFromCache(){
        PaginationResponse<BookingDto> cachedResponse = new PaginationResponse<>();
        cachedResponse.setContent(List.of(testbookingDto, testbookingDto2));

        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseUser);
        when(cacheService.findUserPaginationBookings(testValidationResponseUser.getUserId(), pageable)).thenReturn(Optional.of(cachedResponse));

        BookingResponse<PaginationResponse<BookingDto>> result = bookingService.getUserBookings(pageable, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(cacheService, times(1)).findUserPaginationBookings(testValidationResponseUser.getUserId(), pageable);
        verify(bookingRepository, never()).findAllByUserId(testValidationResponseUser.getUserId(), pageable);
        verify(cacheService, never()).savePaginationBookingDto(anyString(), any(PaginationResponse.class));
        verify(bookingMapper, never()).toBookingDto(any(Booking.class));
    }

    @Test
    void getUserBookings_IncorrectRole_ThrowIncorrectRoleException(){
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);

        assertThatThrownBy(() -> bookingService.getUserBookings(pageable, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("forbidden");

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(cacheService, never()).findUserPaginationBookings(testValidationResponseUser.getUserId(), pageable);
        verify(bookingRepository, never()).findAllByUserId(testValidationResponseUser.getUserId(), pageable);
        verify(cacheService, never()).savePaginationBookingDto(anyString(), any(PaginationResponse.class));
        verify(bookingMapper, never()).toBookingDto(any(Booking.class));
    }

    @Test
    void getOwnerBookings_SuccessFromDB(){
        List<Booking> bookingList = List.of(testBooking, testBooking2);
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());

        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);  // OWNER role
        when(restaurantServiceClient.getRestaurantIdWhenUserIsOwner(testValidationResponseOwner.getUserId())).thenReturn(1);
        when(cacheService.findOwnerPaginationBookings(testValidationResponseOwner.getUserId(), 1, pageable)).thenReturn(Optional.empty());
        when(bookingRepository.findAllByRestaurantId(1, pageable)).thenReturn(bookingPage);
        when(bookingMapper.toBookingDto(testBooking)).thenReturn(testbookingDto);
        when(bookingMapper.toBookingDto(testBooking2)).thenReturn(testbookingDto2);

        BookingResponse<PaginationResponse<BookingDto>> result = bookingService.getOwnerBookings(
                pageable, jwtToken, refreshToken, response
        );

        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertEquals(2, result.getPayload().getContent().size());
        assertEquals(testbookingDto.getId(), result.getPayload().getContent().get(0).getId());
        assertEquals(testbookingDto2.getId(), result.getPayload().getContent().get(1).getId());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, times(1)).getRestaurantIdWhenUserIsOwner(testValidationResponseOwner.getUserId());
        verify(cacheService, times(1)).findOwnerPaginationBookings(testValidationResponseOwner.getUserId(), 1, pageable);
        verify(bookingRepository, times(1)).findAllByRestaurantId(1, pageable);
        verify(bookingMapper, times(2)).toBookingDto(any(Booking.class));
    }

    @Test
    void getOwnerBookings_SuccessFromCache(){
        PaginationResponse<BookingDto> cachedResponse = new PaginationResponse<>();
        cachedResponse.setContent(List.of(testbookingDto, testbookingDto2));

        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(restaurantServiceClient.getRestaurantIdWhenUserIsOwner(testValidationResponseOwner.getUserId())).thenReturn(1);
        when(cacheService.findOwnerPaginationBookings(testValidationResponseOwner.getUserId(), 1, pageable)).thenReturn(Optional.of(cachedResponse));

        BookingResponse<PaginationResponse<BookingDto>> result = bookingService.getOwnerBookings(pageable, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, times(1)).getRestaurantIdWhenUserIsOwner(testValidationResponseOwner.getUserId());
        verify(cacheService, times(1)).findOwnerPaginationBookings(testValidationResponseOwner.getUserId(), 1, pageable);
        verify(bookingRepository, never()).findAllByRestaurantId(1, pageable);
        verify(bookingMapper, never()).toBookingDto(any(Booking.class));
    }

    @Test
    void getOwnerBookings_IncorrectRole_ThrowIncorrectRoleException() {
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseUser);

        assertThatThrownBy(() -> bookingService.getOwnerBookings(pageable, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("forbidden");

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(restaurantServiceClient, never()).getRestaurantIdWhenUserIsOwner(testValidationResponseOwner.getUserId());
        verify(cacheService, never()).findOwnerPaginationBookings(testValidationResponseOwner.getUserId(), 1, pageable);
        verify(bookingRepository, never()).findAllByRestaurantId(1, pageable);
        verify(bookingMapper, never()).toBookingDto(any(Booking.class));
    }

    @Test
    void updateBooking_Success(){
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(restaurantServiceClient.getRestaurantIdAndCheckOwner(testBooking.getRestaurantId(), testValidationResponseOwner.getUserId(), testUpdateBookingRequest.getTableNumber())).thenReturn(testRestaurantCheckResponse);
        when(bookingRepository.existsConflictingBookingExcludingId(testBooking.getRestaurantId(), testUpdateBookingRequest.getTableNumber(), testUpdateBookingRequest.getBookingFrom(), testUpdateBookingRequest.getBookingTo(), 1L)).thenReturn(false);
        when(bookingMapper.updatedBookingRequestToBooking(testBooking, testUpdateBookingRequest)).thenReturn(testBooking);
        when(bookingRepository.save(testBooking)).thenReturn(testBooking);
        when(bookingMapper.toBookingDto(testBooking)).thenReturn(testbookingDto);

        BookingResponse<BookingDto> result = bookingService.updateBooking(1L, testUpdateBookingRequest, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertEquals(testbookingDto.getId(), result.getPayload().getId());
        assertEquals(testbookingDto.getUserId(), result.getPayload().getUserId());
        assertEquals(testbookingDto.getRestaurantId(), result.getPayload().getRestaurantId());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(bookingRepository, times(1)).findById(1L);
        verify(restaurantServiceClient, times(1)).getRestaurantIdAndCheckOwner(testBooking.getRestaurantId(), testValidationResponseOwner.getUserId(), testUpdateBookingRequest.getTableNumber());
        verify(bookingRepository, times(1)).existsConflictingBookingExcludingId(testBooking.getRestaurantId(),testUpdateBookingRequest.getTableNumber(), testUpdateBookingRequest.getBookingFrom(),testUpdateBookingRequest.getBookingTo(), 1L);
        verify(bookingMapper, times(1)).updatedBookingRequestToBooking(testBooking, testUpdateBookingRequest);
        verify(bookingRepository, times(1)).save(testBooking);
        verify(cacheService, times(1)).deleteBooking(1L);
        verify(bookingMapper, times(1)).toBookingDto(testBooking);
    }

    @Test
    void updateBooking_TimeBookingConflict_ThrowBookingConflictException() {
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(restaurantServiceClient.getRestaurantIdAndCheckOwner(testBooking.getRestaurantId(), testValidationResponseOwner.getUserId(), testUpdateBookingRequest.getTableNumber())).thenReturn(testRestaurantCheckResponse);
        when(bookingRepository.existsConflictingBookingExcludingId(testBooking.getRestaurantId(), testUpdateBookingRequest.getTableNumber(), testUpdateBookingRequest.getBookingFrom(), testUpdateBookingRequest.getBookingTo(), 1L)).thenReturn(true);  // конфликт существует

        assertThatThrownBy(() -> bookingService.updateBooking(1L, testUpdateBookingRequest, jwtToken, refreshToken, response))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("time was busy");

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(bookingRepository, times(1)).findById(1L);
        verify(restaurantServiceClient, times(1)).getRestaurantIdAndCheckOwner(testBooking.getRestaurantId(), testValidationResponseOwner.getUserId(), testUpdateBookingRequest.getTableNumber());
        verify(bookingRepository, times(1)).existsConflictingBookingExcludingId(testBooking.getRestaurantId(), testUpdateBookingRequest.getTableNumber(), testUpdateBookingRequest.getBookingFrom(), testUpdateBookingRequest.getBookingTo(), 1L);
        verify(bookingMapper, never()).updatedBookingRequestToBooking(any(), any());
        verify(bookingRepository, never()).save(any());
        verify(cacheService, never()).deleteBooking(1L);
        verify(bookingMapper, never()).toBookingDto(any());
    }

    @Test
    void softDeleteBooking_Success_WhenUserIsOwner() {
        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponseOwner);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(restaurantServiceClient.checkRestaurantOwner(testBooking.getRestaurantId(), testValidationResponseOwner.getUserId())).thenReturn(true);  // пользователь - владелец ресторана

        bookingService.softDeleteBooking(1L, jwtToken, refreshToken, response);

        assertEquals(BookingStatus.CANCELED, testBooking.getStatus());
        assertNotNull(testBooking.getUpdatedAt());

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(bookingRepository, times(1)).findById(1L);
        verify(restaurantServiceClient, times(1)).checkRestaurantOwner(testBooking.getRestaurantId(), testValidationResponseOwner.getUserId());
        verify(cacheService, times(1)).deleteBooking(1L);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void softDeleteBooking_NotOwnerNotIllegalUser_ThrowIncorrectRoleException() {
        AuthenticationValidationResponse noPermissionResponse = new AuthenticationValidationResponse();
        noPermissionResponse.setValid(true);
        noPermissionResponse.setRoles(List.of("USER"));
        noPermissionResponse.setUserId(999);

        when(authenticationUtils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(noPermissionResponse);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(restaurantServiceClient.checkRestaurantOwner(testBooking.getRestaurantId(), noPermissionResponse.getUserId())).thenReturn(false);  // НЕ владелец ресторана

        assertThatThrownBy(() -> bookingService.softDeleteBooking(1L, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("dont have permission");

        verify(authenticationUtils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(bookingRepository, times(1)).findById(1L);
        verify(restaurantServiceClient, times(1)).checkRestaurantOwner(testBooking.getRestaurantId(), noPermissionResponse.getUserId());
        verify(bookingRepository, never()).save(any());
        verify(cacheService, never()).deleteBooking(1L);
    }
}
