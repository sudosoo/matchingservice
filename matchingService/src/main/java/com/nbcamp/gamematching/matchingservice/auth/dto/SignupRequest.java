package com.nbcamp.gamematching.matchingservice.auth.dto;

import lombok.Getter;

@Getter
public class SignupRequest {

    private String email;
    private String password;
    private String nickname;
    private String memberImageUrl;


}

