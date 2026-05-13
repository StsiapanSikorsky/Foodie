package com.Foodie.authentivation_service.controllers;

import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.requests.user.UpdateUserRequest;
import com.Foodie.authentivation_service.responce.user.UserResponse;
import com.Foodie.authentivation_service.services.UserService;
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
public class UserController {

    private final UserService userService;

    @GetMapping("${end.point.id}")
    public ResponseEntity<UserResponse<UserDto>> getUserById(
            @PathVariable (name = "id") Integer id
    ){
        UserResponse<UserDto> response = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("${end.point.id}")
    public ResponseEntity<UserResponse<UserDto>> updateUser(
            @PathVariable (name = "id") Integer id,
            @RequestBody @Valid UpdateUserRequest request
    ){
        UserResponse<UserDto> response = userService.updateUser(id,request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("${end.point.id}")
    public ResponseEntity<UserResponse<UserDto>> softDeleteUser(
            @PathVariable (name = "id") Integer id
    ){
        userService.softDeleteUser(id);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
