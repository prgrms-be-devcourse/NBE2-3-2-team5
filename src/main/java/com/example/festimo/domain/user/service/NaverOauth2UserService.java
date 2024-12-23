package com.example.festimo.domain.user.service;

import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.dto.NaverOauth2Response;
import com.example.festimo.domain.user.dto.NaverOauth2User;
import com.example.festimo.domain.user.dto.UserResponseDTO;
import com.example.festimo.domain.user.dto.UserTO;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class NaverOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        NaverOauth2Response naverResponse = null;
        if (registrationId.equals("naver")) {
            naverResponse = new NaverOauth2Response(oAuth2User.getAttributes());
        }else{
            return null;
        }
        String username = naverResponse.getEmail();
        User user = userRepository.findByUserName(username);
        if (user == null) {
            user = new User();
            user.setUserName(username);
            user.setRole(User.Role.USER);
            user.setNickname(naverResponse.getName());
            user.setEmail(naverResponse.getEmail());
            if (naverResponse.getGender().equals("M")) {
                user.setGender(User.Gender.M);
            } else {
                user.setGender(User.Gender.F);
            }
            user.setRefreshToken(jwtTokenProvider.generateRefreshToken(naverResponse.getEmail()));

            userRepository.save(user);

            UserTO to = new UserTO();
            to.setUsername(username);
            to.setRole(String.valueOf(user.getRole()));
            to.setNickname(naverResponse.getName());
            to.setEmail(naverResponse.getEmail());

            return new NaverOauth2User(to);

        }else{
            user.setProvider(User.Provider.NAVER);
            user.setEmail(naverResponse.getEmail());
            user.setProviderId(naverResponse.getProviderId());
            user.setRefreshToken(jwtTokenProvider.generateRefreshToken(naverResponse.getEmail()));
            userRepository.save(user);

            UserTO to = new UserTO();
            to.setUsername(username);
            to.setRole(String.valueOf(user.getRole()));
            to.setNickname(naverResponse.getName());
            return new NaverOauth2User(to);
        }

    }
}
