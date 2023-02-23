package com.example.vilkipalki2.repos;

import com.example.vilkipalki2.models.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<EmailTemplate, Integer> {

    Optional<EmailTemplate> findByName(String name);
}
