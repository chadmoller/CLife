package com.clife.identity.domain;

import com.googlecode.objectify.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    public Long id;
    @Index
    public String email;
    public String fullName;
    public String nickname;
    public Date birthdate;
    public byte[] profilePicture;
    public boolean enabled;
    public Set<Authority> authorities = new HashSet<>();

    public String getFullName() {
        return fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setFullName(String fn) {
        this.fullName = fn;
    }

    public void setNickname(String nn) {
        this.nickname = nn;
    }
}
