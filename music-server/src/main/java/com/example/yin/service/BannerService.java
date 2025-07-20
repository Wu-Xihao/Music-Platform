package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Banner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService extends IService<Banner> {

    List<Banner> getAllBanner();

    R addBanner(MultipartFile picFile);
    R deleteBanner(Integer id);
    R updateBannerImg(MultipartFile file, Integer id);

}
