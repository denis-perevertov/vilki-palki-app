package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.Banner;
import com.example.vilkipalki2.repos.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/banners")
public class BannerController {
    
    @Autowired
    private BannerRepository repo;

    @GetMapping
    public List<Banner> getBanners() {
        return repo.findAll();
    }
}
