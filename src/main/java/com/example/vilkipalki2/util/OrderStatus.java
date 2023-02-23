package com.example.vilkipalki2.util;

import java.util.HashMap;
import java.util.Map;

public enum OrderStatus {
    NEW(0),
    ACCEPTED(1),
    PREPARING(2),
    ON_THE_WAY(3),
    COMPLETED(4),
    CANCELED(100);

    private final int number;

    private static final Map<Integer, OrderStatus> lookup = new HashMap<>();

    static {
        for(OrderStatus st : OrderStatus.values()) {
            lookup.put(st.getNumber(), st);
        }
    }

    OrderStatus(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean current() {return !(this.equals(OrderStatus.COMPLETED) || this.equals(OrderStatus.CANCELED));}

    public static OrderStatus getStatusByNumber(int number) {return lookup.getOrDefault(number, OrderStatus.CANCELED);}

}
