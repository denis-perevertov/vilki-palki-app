package com.example.vilkipalki2.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public @ResponseBody String handleError(HttpServletRequest request) {

        return "UNKNOWN ERROR";
    }

}
