package com.Foodie.restaurant_service.unit;


import com.Foodie.restaurant_service.advice.exceptions.DataExistsException;
import com.Foodie.restaurant_service.advice.exceptions.IncorrectRoleException;
import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.dto.RestaurantTableDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.entity.RestaurantTable;
import com.Foodie.restaurant_service.mapper.RestaurantTableMapper;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.repository.RestaurantTableRepository;
import com.Foodie.restaurant_service.request.tables.TableRequest;
import com.Foodie.restaurant_service.request.tables.UpdateTableRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantTableResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.impl.RestaurantTableServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RestaurantTableServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTableMapper restaurantTableMapper;

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private Utils utils;

    @InjectMocks
    private RestaurantTableServiceImpl restaurantTableService;

    private Restaurant testRestaurant;
    private RestaurantTable testRestaurantTable;
    private RestaurantTable testRestaurantTable2;
    private RestaurantTableDto testRestaurantTableDto;
    private RestaurantTableDto testRestaurantTableDto2;
    private TableRequest testTableRequest;
    private UpdateTableRequest testUpdateTableRequest;
    private RestaurantTable testUpdateRestaurantTable;
    private RestaurantTableDto testUpdateRestaurantTableDto;
    private AuthenticationValidationResponse testValidationResponse;

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

        testRestaurantTable = new RestaurantTable();
        testRestaurantTable.setId(1L);
        testRestaurantTable.setNumberOfTable(1);
        testRestaurantTable.setRestaurant(testRestaurant);

        testRestaurantTable2 = new RestaurantTable();
        testRestaurantTable2.setId(2L);
        testRestaurantTable2.setNumberOfTable(2);
        testRestaurantTable2.setRestaurant(testRestaurant);

        testRestaurantTableDto = new RestaurantTableDto();
        testRestaurantTableDto.setId(1L);
        testRestaurantTableDto.setRestaurantId(1);
        testRestaurantTableDto.setNumberOfTable(1);

        testRestaurantTableDto2 = new RestaurantTableDto();
        testRestaurantTableDto2.setId(2L);
        testRestaurantTableDto2.setRestaurantId(1);
        testRestaurantTableDto2.setNumberOfTable(2);

        testTableRequest = new TableRequest();
        testTableRequest.setNumberOfTable(1);

        testUpdateTableRequest = new UpdateTableRequest();
        testUpdateTableRequest.setNumberOfTable(2);

        testUpdateRestaurantTable = new RestaurantTable();
        testUpdateRestaurantTable.setId(1L);
        testUpdateRestaurantTable.setNumberOfTable(2);
        testUpdateRestaurantTable.setRestaurant(testRestaurant);

        testUpdateRestaurantTableDto = new RestaurantTableDto();
        testUpdateRestaurantTableDto.setId(1L);
        testUpdateRestaurantTableDto.setRestaurantId(1);
        testUpdateRestaurantTableDto.setNumberOfTable(2);

        testValidationResponse = new AuthenticationValidationResponse();
        testValidationResponse.setValid(true);
        testValidationResponse.setRoles(List.of("OWNER"));
        testValidationResponse.setUserId(1);

        pageable = PageRequest.of(0,10);
    }

    @Test
    void addRestaurantTable_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantTableRepository.existsByRestaurantIdAndNumberOfTable(1, testTableRequest.getNumberOfTable())).thenReturn(false);
        when(restaurantTableMapper.tableRequestToRestaurantTable(testTableRequest, testRestaurant)).thenReturn(testRestaurantTable);
        when(restaurantTableRepository.save(testRestaurantTable)).thenReturn(testRestaurantTable);
        when(restaurantTableMapper.toRestaurantTableDto(testRestaurantTable)).thenReturn(testRestaurantTableDto);

        RestaurantTableResponse<RestaurantTableDto> result = restaurantTableService.addRestaurantTable(1, testTableRequest, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertEquals(result.getPayload().getNumberOfTable(), 1);
        assertEquals(result.getPayload().getRestaurantId(), testRestaurant.getId());

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, times(1)).existsByRestaurantIdAndNumberOfTable(1, testTableRequest.getNumberOfTable());
        verify(restaurantTableMapper, times(1)).tableRequestToRestaurantTable(testTableRequest, testRestaurant);
        verify(restaurantTableRepository, times(1)).save(testRestaurantTable);
        verify(restaurantTableMapper, times(1)).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void addRestaurantTable_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.addRestaurantTable(999, testTableRequest, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, never()).existsByRestaurantIdAndNumberOfTable(1, testTableRequest.getNumberOfTable());
        verify(restaurantTableMapper, never()).tableRequestToRestaurantTable(testTableRequest, testRestaurant);
        verify(restaurantTableRepository, never()).save(testRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void addRestaurantTable_NotOwner_ThrowIncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantTableService.addRestaurantTable(1, testTableRequest, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, never()).existsByRestaurantIdAndNumberOfTable(1, testTableRequest.getNumberOfTable());
        verify(restaurantTableMapper, never()).tableRequestToRestaurantTable(testTableRequest, testRestaurant);
        verify(restaurantTableRepository, never()).save(testRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void addRestaurantTable_ThrowDataExistsException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantTableRepository.existsByRestaurantIdAndNumberOfTable(1, testTableRequest.getNumberOfTable())).thenReturn(true);

        assertThatThrownBy(() -> restaurantTableService.addRestaurantTable(1, testTableRequest, jwtToken, refreshToken, response))
                .isInstanceOf(DataExistsException.class)
                .hasMessageContaining("already exists");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, times(1)).existsByRestaurantIdAndNumberOfTable(1, testTableRequest.getNumberOfTable());
        verify(restaurantTableMapper, never()).tableRequestToRestaurantTable(testTableRequest, testRestaurant);
        verify(restaurantTableRepository, never()).save(testRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void getAllTables_Success_returnNotEmpty(){
        List<RestaurantTable> tables = List.of(testRestaurantTable, testRestaurantTable2);
        Page<RestaurantTable> taablesPage = new PageImpl<>(tables, pageable, tables.size());

        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantTableRepository.findByRestaurantId(1, pageable)).thenReturn(taablesPage);
        when(restaurantTableMapper.toRestaurantTableDto(testRestaurantTable)).thenReturn(testRestaurantTableDto);
        when(restaurantTableMapper.toRestaurantTableDto(testRestaurantTable2)).thenReturn(testRestaurantTableDto2);

        RestaurantTableResponse<PaginationResponse<RestaurantTableDto>> result = restaurantTableService.getAllTables(1, pageable);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        PaginationResponse<RestaurantTableDto> paginationResponse = result.getPayload();
        List<RestaurantTableDto> content = paginationResponse.getContent();

        assertEquals(2, content.size());
        assertEquals(testRestaurantTableDto.getNumberOfTable(), content.get(0).getNumberOfTable());
        assertEquals(testRestaurantTableDto2.getNumberOfTable(), content.get(1).getNumberOfTable());

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, times(1)).findByRestaurantId(1, pageable);
        verify(restaurantTableMapper, times(1)).toRestaurantTableDto(testRestaurantTable);
        verify(restaurantTableMapper, times(1)).toRestaurantTableDto(testRestaurantTable2);
    }

    @Test
    void getAllTables_Success_returnEmpty(){
        List<RestaurantTable> tables = List.of();
        Page<RestaurantTable> taablesPage = new PageImpl<>(tables, pageable, 0);

        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantTableRepository.findByRestaurantId(1, pageable)).thenReturn(taablesPage);

        RestaurantTableResponse<PaginationResponse<RestaurantTableDto>> result = restaurantTableService.getAllTables(1, pageable);

        assertNotNull(result);
        assertNotNull(result.getPayload());

        PaginationResponse<RestaurantTableDto> paginationResponse = result.getPayload();
        List<RestaurantTableDto> content = paginationResponse.getContent();

        assertTrue(content.isEmpty());

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, times(1)).findByRestaurantId(1, pageable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable2);
    }

    @Test
    void getAllTables_TrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.getAllTables(999, pageable))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(restaurantTableRepository, never()).findByRestaurantId(1, pageable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable2);
    }

    @Test
    void getTable_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantTableRepository.findByRestaurantIdAndNumberOfTable(1, 1)).thenReturn(Optional.of(testRestaurantTable));
        when(restaurantTableMapper.toRestaurantTableDto(testRestaurantTable)).thenReturn(testRestaurantTableDto);

        RestaurantTableResponse<RestaurantTableDto> result = restaurantTableService.getTable(1, 1);

        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertEquals(result.getPayload().getNumberOfTable(), 1);
        assertEquals(result.getPayload().getRestaurantId(), 1);

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, times(1)).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableMapper, times(1)).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void getTable_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.getTable(1, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, never()).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void getTable_TableNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantTableRepository.findByRestaurantIdAndNumberOfTable(1, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.getTable(1, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(restaurantTableRepository, times(1)).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testRestaurantTable);
    }

    @Test
    void updateTable_changeNumberOfTable_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantTableRepository.findByRestaurantIdAndNumberOfTable(1,1)).thenReturn(Optional.of(testRestaurantTable));
        when(restaurantTableMapper.updatedTableRequestToRestaurantTable(testRestaurantTable, testUpdateTableRequest)).thenReturn(testUpdateRestaurantTable);
        when(restaurantTableRepository.save(testUpdateRestaurantTable)).thenReturn(testUpdateRestaurantTable);
        when(restaurantTableMapper.toRestaurantTableDto(testUpdateRestaurantTable)).thenReturn(testUpdateRestaurantTableDto);

        RestaurantTableResponse<RestaurantTableDto> result = restaurantTableService.updateTable(1, 1, testUpdateTableRequest, jwtToken, refreshToken, response);

        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertEquals(2, result.getPayload().getNumberOfTable());

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, times(1)).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableMapper, times(1)).updatedTableRequestToRestaurantTable(testRestaurantTable, testUpdateTableRequest);
        verify(restaurantTableRepository, times(1)).save(testUpdateRestaurantTable);
        verify(restaurantTableMapper, times(1)).toRestaurantTableDto(testUpdateRestaurantTable);
    }

    @Test
    void updateTable_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.updateTable(999, 1, testUpdateTableRequest, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, never()).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableMapper, never()).updatedTableRequestToRestaurantTable(testRestaurantTable, testUpdateTableRequest);
        verify(restaurantTableRepository, never()).save(testUpdateRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testUpdateRestaurantTable);
    }

    @Test
    void updateTable_NotOwner_ThrowIncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantTableService.updateTable(1, 1, testUpdateTableRequest, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("You don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, never()).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableMapper, never()).updatedTableRequestToRestaurantTable(testRestaurantTable, testUpdateTableRequest);
        verify(restaurantTableRepository, never()).save(testUpdateRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testUpdateRestaurantTable);
    }

    @Test
    void updateTable_TableNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantTableRepository.findByRestaurantIdAndNumberOfTable(1,999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.updateTable(1, 999, testUpdateTableRequest, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, times(1)).findByRestaurantIdAndNumberOfTable(1,999);
        verify(restaurantTableMapper, never()).updatedTableRequestToRestaurantTable(testRestaurantTable, testUpdateTableRequest);
        verify(restaurantTableRepository, never()).save(testUpdateRestaurantTable);
        verify(restaurantTableMapper, never()).toRestaurantTableDto(testUpdateRestaurantTable);
    }

    @Test
    void deleteTable_Success(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantTableRepository.findByRestaurantIdAndNumberOfTable(1,1)).thenReturn(Optional.of(testRestaurantTable));

        restaurantTableService.deleteTable(1, 1, jwtToken, refreshToken, response);

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, times(1)).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableRepository, times(1)).delete(testRestaurantTable);
    }

    @Test
    void deleteTable_RestaurantNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.deleteTable(999, 1, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(utils, never()).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, never()).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, never()).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableRepository, never()).delete(testRestaurantTable);
    }

    @Test
    void deleteTable_NotOwner_ThrowIncorrectRoleException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(false);

        assertThatThrownBy(() -> restaurantTableService.deleteTable(1, 1, jwtToken, refreshToken, response))
                .isInstanceOf(IncorrectRoleException.class)
                .hasMessageContaining("You don't have permission");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, never()).findByRestaurantIdAndNumberOfTable(1,1);
        verify(restaurantTableRepository, never()).delete(testRestaurantTable);
    }

    @Test
    void deleteTable_TableNotFound_ThrowNotFoundException(){
        when(restaurantRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testRestaurant));
        when(utils.checkValidTokens(jwtToken, refreshToken, response)).thenReturn(testValidationResponse);
        when(utils.isOwnerOrAdmin(testRestaurant, testValidationResponse)).thenReturn(true);
        when(restaurantTableRepository.findByRestaurantIdAndNumberOfTable(1,999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantTableService.deleteTable(1, 999, jwtToken, refreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(restaurantRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(utils, times(1)).checkValidTokens(jwtToken, refreshToken, response);
        verify(utils, times(1)).isOwnerOrAdmin(testRestaurant, testValidationResponse);
        verify(restaurantTableRepository, times(1)).findByRestaurantIdAndNumberOfTable(1,999);
        verify(restaurantTableRepository, never()).delete(testRestaurantTable);
    }
}
