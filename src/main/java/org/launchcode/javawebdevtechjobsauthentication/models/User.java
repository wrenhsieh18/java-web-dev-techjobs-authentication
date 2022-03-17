package org.launchcode.javawebdevtechjobsauthentication.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Entity;

@Entity
public class User extends AbstractEntity {

    private String username;

    private String pwHash;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User () {};

    public User(String username, String password) {
        this.username = username;
        this.pwHash = encoder.encode(password);
    }

    public String getUsername() {
        return username;
    }

    public boolean isMatchingPassword(String password) {
        return encoder.matches(password, this.pwHash);
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }

//    public String getPwHash() {
//        return pwHash;
//    }
//
//    public void setPwHash(String password) {
//        this.pwHash = encoder.encode(password);
//    }
}
