package com.tigeranalytics.pricingfeed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.List;

public class UserInfoDetails implements UserDetails, Serializable {

    private static final long serialversionUID = 129348938L;
    private Long userAccountNumber;
    private String username; // Changed from 'name' to 'username' for clarity
    @JsonIgnore
    private String password;
    private String email;
    @JsonIgnore
    private List<GrantedAuthority> authorities;

    public Long getUserAccountNumber() {
        return userAccountNumber;
    }

    public void setUserAccountNumber(Long userAccountNumber) {
        this.userAccountNumber = userAccountNumber;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
