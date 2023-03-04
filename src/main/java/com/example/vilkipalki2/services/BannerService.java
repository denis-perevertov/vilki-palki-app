package com.example.vilkipalki2.services;

import com.example.vilkipalki2.models.Banner;
import com.example.vilkipalki2.repos.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<Banner> getAllBanners() {return bannerRepository.findAll();}

    public Banner saveBanner(Banner banner) {return bannerRepository.save(banner);}

    public void deleteBanner(long id) {
        System.out.println("deleteBanner");
        bannerRepository.deleteById(id);
        System.out.println("Banner deleted?");
    }
}
