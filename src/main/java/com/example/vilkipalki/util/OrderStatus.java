package com.example.vilkipalki.util;

public enum OrderStatus {
    NEW(0),
    ACCEPTED(1),
    PREPARING(2),
    ON_THE_WAY(3),
    COMPLETED(4),
    CANCELED(100);

    private int number;

    OrderStatus(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
