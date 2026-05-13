package com.Foodie.authentivation_service.requests.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UpdateUserRequest implements Serializable {

    private String userName;
    private String email;
    private String password;

}
