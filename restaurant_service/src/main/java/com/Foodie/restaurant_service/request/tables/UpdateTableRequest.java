package com.Foodie.restaurant_service.request.tables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTableRequest {

    private Integer numberOfTable;
    private String description;
    private Integer capacity;
}
