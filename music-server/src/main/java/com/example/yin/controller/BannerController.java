package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.domain.Banner;
import com.example.yin.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/getAllBanner")
    public R getAllBanner(){
        return R.success("成功获取轮播图与",bannerService.getAllBanner());
    }

    @PostMapping("/addBanner")
    public R addBanner(@RequestParam("file") MultipartFile file) {
        return bannerService.addBanner(file);
    }

    @DeleteMapping("/deleteBanner/{id}")
    public R deleteBanner(@PathVariable("id") Integer id) {
        return bannerService.deleteBanner(id);
    }

    @PostMapping("/updateBannerImg/{id}")
    public R updateBannerImg(@RequestParam("file") MultipartFile file, @PathVariable("id") Integer id) {
        return bannerService.updateBannerImg(file, id);
    }
}
