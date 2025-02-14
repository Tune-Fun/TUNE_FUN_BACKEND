package com.tune_fun.v1.account.domain.value.oauth2;

import com.tune_fun.v1.common.exception.OAuth2AuthenticationProcessingException;
import lombok.experimental.UtilityClass;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@UtilityClass
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(final String registrationId, final String accessToken, final Map<String, Object> attributes) throws NoSuchAlgorithmException {
        return switch (OAuth2Provider.fromRegistrationId(registrationId)) {
            case GOOGLE -> new GoogleOAuth2UserInfo(accessToken, attributes);
            case APPLE -> new AppleOAuth2UserInfo(accessToken, attributes);
            case INSTAGRAM -> new InstagramOAuth2UserInfo(accessToken, attributes);
            case null ->
                    throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
        };
    }

}
