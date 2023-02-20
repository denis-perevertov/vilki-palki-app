package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.services.email.EmailServiceImpl;
import com.example.vilkipalki2.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@Log
public class MailController {

    private final EmailServiceImpl emailService;
    private final UserService userService;

    @GetMapping("/admin/email")
    public String showEmailPage(Model model) {
        model.addAttribute("users", userService.getUsersDTO());
        model.addAttribute("templates", emailService.getTemplates());
        return "admin_panel/email";
    }

    @PostMapping("/admin/email")
    public @ResponseBody String sendMailTest(@RequestParam String users, @RequestParam String template_name) {

        String templateWithoutExtension = template_name.split("[\"\\.]")[1];

        log.info("Sending template: "  +templateWithoutExtension);

        String[] ids = users.split("\\D+");
        String[] emailReceivers = Arrays.stream(ids).filter((id) -> !id.isBlank()).map((id) -> {
            long user_id = Long.parseLong(id);
            return userService.getUserEmailById(user_id);
        }).toArray(String[]::new);
        log.info(Arrays.toString(ids));
        log.info(Arrays.toString(emailReceivers));

        emailService.sendMailTest(emailReceivers, templateWithoutExtension);

        return "Success";
    }

    @PostMapping("/admin/email/save-template")
    public @ResponseBody String saveTemplateToDB(@RequestParam String template_name) {
        emailService.saveTemplateToDB(template_name);
        return "Saved template successfully";
    }

}
