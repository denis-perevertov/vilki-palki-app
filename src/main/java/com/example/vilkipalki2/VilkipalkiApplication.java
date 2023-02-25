package com.example.vilkipalki2;

import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.repos.CategoryRepository;
import com.example.vilkipalki2.repos.MenuItemRepository;
import com.example.vilkipalki2.util.AppUserRole;
import com.example.vilkipalki2.util.Gender;
import com.example.vilkipalki2.util.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class VilkipalkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VilkipalkiApplication.class, args);
	}


}
