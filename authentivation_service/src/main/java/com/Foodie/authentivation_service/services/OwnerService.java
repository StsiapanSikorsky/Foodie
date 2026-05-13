package com.Foodie.authentivation_service.services;

import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.requests.owner.UpdateOwnerRequest;
import com.Foodie.authentivation_service.responce.owner.OwnerResponse;
import jakarta.validation.constraints.NotNull;

public interface OwnerService {

    OwnerResponse<UserDto> getOwnerById(@NotNull Integer id);

    OwnerResponse<UserDto> updateOwner(@NotNull Integer id, UpdateOwnerRequest request);

    void softDeleteOwner(@NotNull Integer id);
}
