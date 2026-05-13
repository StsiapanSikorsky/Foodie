package com.Foodie.authentivation_service.utils;

import com.Foodie.authentivation_service.enums.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
public class UtilsRefreshToken {

    public static String generateUuidWithoutDash(){
        return UUID.randomUUID().toString().replace(Constants.DASH, StringUtils.EMPTY);
    }
}
