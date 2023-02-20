package com.example.vilkipalki2.repos;

import com.example.vilkipalki2.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query(value = "SELECT it.pictureFileName FROM MenuItem it WHERE it.id = ?1")
    String getPictureById(long id);
}
