package com.Foodie.restaurant_service.request.tables;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {

    @NotNull
    private Integer numberOfTable;

    private String description;

    private Integer capacity;
}
