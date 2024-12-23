package com.example.festimo.domain.user.dto;

import java.util.Map;

public class NaverOauth2Response {

    public final Map<String, Object> attribute;
    public NaverOauth2Response(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }
    public String getProvider(){
        return "naver";
    }
    public String getProviderId() {

        return attribute.get("id").toString();
    }
    public String getEmail() {

        return attribute.get("email").toString();
    }
    public String getName() {

        return attribute.get("name").toString();
    }
    public String getGender(){
        return attribute.get("gender").toString();
    }

}
