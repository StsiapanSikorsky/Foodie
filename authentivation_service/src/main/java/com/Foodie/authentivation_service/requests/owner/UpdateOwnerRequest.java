package com.Foodie.authentivation_service.requests.owner;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UpdateOwnerRequest implements Serializable {

    private String userName;
    private String email;
    private String password;

}
