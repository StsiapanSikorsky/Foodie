package com.Foodie.restaurant_service.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> implements Serializable {

    private List<T> content;
    private Pagination pagination;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination implements Serializable{
        private long total;
        private int limit;
        private int page;
        private int pages;
    }
}
