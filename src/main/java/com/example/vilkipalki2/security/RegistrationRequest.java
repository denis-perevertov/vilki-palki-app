package com.example.vilkipalki2.security;

import java.util.Objects;

public class RegistrationRequest {

    private final String login;
    private final String password;

    public RegistrationRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(login, that.login) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
