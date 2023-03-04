package com.example.vilkipalki2.exception;

public class ItemNotFoundException extends RuntimeException{
    public ItemNotFoundException() {
        super("Item not found");
    }
}
