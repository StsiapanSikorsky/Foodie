package com.Foodie.authentivation_service.controllers;

import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.requests.owner.UpdateOwnerRequest;
import com.Foodie.authentivation_service.responce.owner.OwnerResponse;
import com.Foodie.authentivation_service.services.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${end.point.owner}")
@Tag(name = "Owner controller")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("${end.point.id}")
    @Operation(
            summary = "Получение собственника по id",
            description = "Получение собственника с ролью OWNER по id"
    )
    public ResponseEntity<OwnerResponse<UserDto>> getOwnerById(
            @PathVariable (name = "id") Integer id
    ){
        OwnerResponse<UserDto> response = ownerService.getOwnerById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("${end.point.id}")
    @Operation(
            summary = "Обновление данных собственника по id",
            description = "Обновление данных собственника с ролью OWNER по id"
    )
    public ResponseEntity<OwnerResponse<UserDto>> updateOwner(
            @PathVariable (name = "id") Integer id,
            @RequestBody @Valid UpdateOwnerRequest request
    ){
        OwnerResponse<UserDto> response = ownerService.updateOwner(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("${end.point.id}")
    @Operation(
            summary = "Удаление собственника по id",
            description = "Удаление собственника с ролью OWNER по id"
    )
    public ResponseEntity<Void> softDeleteOwner(
            @PathVariable (name = "id") Integer id
    ){
        ownerService.softDeleteOwner(id);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
