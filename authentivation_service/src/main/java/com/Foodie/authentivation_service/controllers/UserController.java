package com.Foodie.authentivation_service.controllers;

import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.enums.LogMessage;
import com.Foodie.authentivation_service.enums.MethodsHTTP;
import com.Foodie.authentivation_service.requests.user.UpdateUserRequest;
import com.Foodie.authentivation_service.responce.user.UserResponse;
import com.Foodie.authentivation_service.services.UserService;
import com.Foodie.authentivation_service.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${end.point.user}")
@Tag(name = "User controller")
public class UserController {

    private final UserService userService;

    @GetMapping("${end.point.id}")
    @Operation(
            summary = "Получение пользователя по id",
            description = "Получение пользователя с ролью USER по id"
    )
    public ResponseEntity<UserResponse<UserDto>> getUserById(
            @PathVariable (name = "id") Integer id
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

        UserResponse<UserDto> response = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("${end.point.id}")
    @Operation(
            summary = "Обновление пользователя по id",
            description = "Обновление данных пользователя с ролью USER по id"
    )
    public ResponseEntity<UserResponse<UserDto>> updateUser(
            @PathVariable (name = "id") Integer id,
            @RequestBody @Valid UpdateUserRequest request
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.PUT, Utils.getMethodName()));

        UserResponse<UserDto> response = userService.updateUser(id,request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("${end.point.id}")
    @Operation(
            summary = "Удаление пользователя по id",
            description = "Удаление пользователя с ролью USER по id"
    )
    public ResponseEntity<UserResponse<UserDto>> softDeleteUser(
            @PathVariable (name = "id") Integer id
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.DELETE, Utils.getMethodName()));

        userService.softDeleteUser(id);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
