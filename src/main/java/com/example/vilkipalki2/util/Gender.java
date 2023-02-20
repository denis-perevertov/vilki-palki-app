package com.example.vilkipalki2.util;

public enum Gender {
    MALE("M"),
    FEMALE("F"),
    NON_BINARY("NB"),
    UNKNOWN("U");

    public final String abbr;

    Gender(String abbr) {
        this.abbr = abbr;
    }
}
