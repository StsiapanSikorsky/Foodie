package com.Foodie.booking_service.utils;

import com.Foodie.booking_service.enums.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
public class Utils {

    public static String getMethodName(){
        try {
            return Thread.currentThread().getStackTrace()[2].getMethodName();
        }
        catch (Exception e) {
            return ErrorMessage.UNDEFINED.getMessage();
        }
    }
}
