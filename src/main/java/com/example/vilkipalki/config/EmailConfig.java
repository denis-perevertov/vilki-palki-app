package com.example.vilkipalki.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Collections;

//@Configuration
//@PropertySource("")
public class EmailConfig implements ApplicationContextAware, EnvironmentAware {

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {

    }

    /*
     *  Message externalization/internationalization for emails.
     *
     *  NOTE we are avoiding the use of the name 'messageSource' for this bean because that
     *       would make the MessageSource defined in SpringWebConfig (and made available for the
     *       web-side template engine) delegate to this one, and thus effectively merge email
     *       messages into web messages and make both types available at the web side, which could
     *       bring undesired collisions.
     *
     *  NOTE also that given we want this specific message source to be used by our
     *       SpringTemplateEngines for emails (and not by the web one), we will set it explicitly
     *       into each of the TemplateEngine objects with 'setTemplateEngineMessageSource(...)'
     */
    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("mail/MailMessages");
        return messageSource;
    }


    /* ******************************************************************** */
    /*  THYMELEAF-SPECIFIC ARTIFACTS FOR EMAIL                              */
    /*  TemplateResolver(3) <- TemplateEngine                               */
    /* ******************************************************************** */

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        // Resolver for HTML emails (except the editable one)
        templateEngine.addTemplateResolver(htmlTemplateResolver());

        // Message source, internationalization specific to emails
        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(2);
        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
