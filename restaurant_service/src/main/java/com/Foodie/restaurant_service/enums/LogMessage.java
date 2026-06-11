package com.Foodie.restaurant_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogMessage {

    //API logs
    METHOD_API_CALLED("%s Called API method: %s"),
    METHOD_API_CALLED_WITH_1_PARAM("Called API method %s with %s"),
    METHOD_API_CALLED_WITH_2_PARAM("Called API method %s with %s, %s"),
    METHOD_API_CALLED_WITH_3_PARAM("Called API method %s with %s, %s, %s"),

    RESTAURANT_CHECK_RESPONSE_SUCCESS("Restaurant check response success: %s"),
    RESTAURANT_FIND_SUCCESS("Restaurant find success with Id:%s"),

    RESTAURANT_CREATE_SUCCESS("User with id: %s has been create restaurant with id: %s"),
    RESTAURANT_UPDATE_SUCCESS("User with id: %s has been update restaurant with id: %s"),
    RESTAURANT_DELETE_SUCCESS("User with id: %s has been delete restaurant with id: %s"),

    UPLOAD_FILE_TO_S3_SUCCESS("File upload to S3 bucket"),
    DELETE_FILE_IN_S3_SUCCESS("File was deleted in S3 bucket"),

    TABLE_CREATE_SUCCESS("User with id: %s has been create table in restaurant with id: %s and number of table: %s"),
    TABLE_UPDATE_SUCCESS("User with id: %s has been update table in restaurant with id: %s and number of table: %s"),
    TABLE_DELETE_SUCCESS("User with id: %s has been delete table in restaurant with id: %s and number of table: %s"),

    JWT_TOKEN_NOT_FOUND("Jwt token not found in cookie. Updating jwt."),
    GET_NEW_JWT_SUCCESS("Get new jwt token success. Save token in cookie"),
    SET_COOKIE("Set cookie: %s"),

    UNDEFINED_METHOD_NAME("Method name was undefined"),



    ;
    private final String message;

    public String getMessage(Object... args){
        return String.format(message,args);
    }
}
