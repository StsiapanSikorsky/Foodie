package com.Foodie.booking_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse<P> implements Serializable {

    private String message;
    private P payload;
    private boolean success;

    public static <P extends Serializable> BookingResponse<P> createSuccessful (P payload){
        return new BookingResponse<>(StringUtils.EMPTY, payload, true);
    }
}
