package com.example.vilkipalki2.repos;

import com.example.vilkipalki2.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String login);

    @Query(value = "SELECT u.email FROM AppUser u WHERE u.id = ?1")
    String findEmailById(long user_id);

}
