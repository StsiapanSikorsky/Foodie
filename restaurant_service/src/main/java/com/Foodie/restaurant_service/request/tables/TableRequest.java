package com.Foodie.restaurant_service.request.tables;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {

    @NotNull
    @Positive
    private Integer numberOfTable;

    private String description;

    @Positive
    private Integer capacity;
}
