package com.senials.security.domain.kakao.auth;

import com.senials.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {
    private User user;
    private Oauth2UserInfo oauth2UserInfo;

    protected PrincipalDetails() {}

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user,Oauth2UserInfo oauth2UserInfo) {
        this.user = user;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.oauth2UserInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
        // 사용자 권한을 반환
        return List.of(new SimpleGrantedAuthority("ROLE_USER")); // 예시 ROLE_USER 권한 반환
        // 실제 구현에서는 사용자 권한을 데이터베이스에서 로드하여 추가
    }

    @Override
    public String getPassword() {
        return user.getUserPwd();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return oauth2UserInfo.getAttributes().get("sub").toString();
    }
}
