package com.senials.security.domain.kakao;

import com.senials.security.domain.kakao.auth.Oauth2UserInfo;

import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {
    private Map<String, Object> attributes;
    private Map<String, Object> attributesAccount;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getProviderId() {
        // Kakao ID는 "id" 키를 사용하여 가져옵니다.
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        // 이메일이 있는지 확인 후 반환
        if (attributesAccount != null && attributesAccount.get("email") != null) {
            return attributesAccount.get("email").toString();
        }
        return null; // 이메일이 없는 경우 null 반환
    }

    @Override
    public String getName() {
        // Kakao API에서 사용자 이름을 가져오는 방식
        if (attributesAccount != null && attributesAccount.get("profile") != null) {
            Map<String, Object> profile = (Map<String, Object>) attributesAccount.get("profile");
            if (profile.get("nickname") != null) {
                return profile.get("nickname").toString(); // "nickname" 키를 사용하여 사용자 이름 반환
            }
        }
        return null; // 이름이 없는 경우 null 반환
    }
}

