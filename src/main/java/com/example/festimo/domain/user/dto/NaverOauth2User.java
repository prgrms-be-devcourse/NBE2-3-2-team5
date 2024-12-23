package com.example.festimo.domain.user.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NaverOauth2User implements OAuth2User {

    private final UserTO userTO;

    public NaverOauth2User(UserTO userTO) {
        this.userTO = userTO;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userTO.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return userTO.getNickname();
    }

    public String getUsername() {
        return userTO.getUsername();
    }
    public String getRole(){
        return userTO.getRole();
    }
    public String getEmail(){
        return userTO.getEmail();
    }
}
