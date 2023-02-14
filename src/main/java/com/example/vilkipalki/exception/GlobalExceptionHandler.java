package com.example.vilkipalki.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleOrderNotFoundException(OrderNotFoundException ex)
    {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, "Order not found").build();
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException ex)
    {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, "Item not found").build();
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleCategoryNotFoundException(CategoryNotFoundException ex)
    {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, "Category not found").build();
    }
}
