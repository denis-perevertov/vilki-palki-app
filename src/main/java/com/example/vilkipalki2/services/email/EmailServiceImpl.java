package com.example.vilkipalki2.services.email;

import com.example.vilkipalki2.models.EmailTemplate;
import com.example.vilkipalki2.repos.TemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ThymeleafService thymeleafService;

    @Autowired
    private final TemplateRepository templateRepository;

    @Value("${spring.mail.username}")
    private String myEmail;

    @Override
    public void sendMailTest(String[] to, String template) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(myEmail);
            helper.setText(thymeleafService.createContent(template, null), true);
            helper.setCc(to);
            helper.setTo(to[0]);
            helper.setSubject("Mail from template HTML");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public List<EmailTemplate> getTemplates() {
        return templateRepository.findAll();
    }

    public void saveTemplateToDB(String template) {
        if(templateRepository.findByName(template).isPresent()) {
            System.out.println("Template already present");
            return;
        }

        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setName(template);

        templateRepository.save(emailTemplate);
    }
}
