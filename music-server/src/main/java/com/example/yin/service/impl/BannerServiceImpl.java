package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.controller.MinioUploadController;
import com.example.yin.mapper.BannerMapper;
import com.example.yin.model.domain.Banner;
import com.example.yin.service.BannerService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService  {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private CacheManager cacheManager;

    @Value("${minio.bucket-name}")
    private String bucketName;

//    @Cacheable(value = "banner", key = "'list'")
    @Override
    public List<Banner> getAllBanner() {
        System.out.println("没有走缓存");
        return bannerMapper.selectList(null);
    }

    // 清除缓存的方法
    private void clearBannerCache() {
        Cache cache = cacheManager.getCache("banner");
        if (cache != null) {
            cache.clear(); // 清除整个缓存
            // 或者清除特定键：cache.evict("list");
        }
    }

    @Override
    public R addBanner(MultipartFile picFile) {
//        if (picFile.isEmpty()) {
//            return R.error("图片不能为空");
//        }
//        try {
//            // 构建存储路径，存入 img/banner/ 下，文件名保持不变
//            String objectName = "/banner/" + picFile.getOriginalFilename();
//            InputStream inputStream = picFile.getInputStream();
//            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(objectName)
//                    .stream(inputStream, picFile.getSize(), -1)
//                    .contentType(picFile.getContentType())
//                    .build();
//            minioClient.putObject(putObjectArgs);
//            String picUrl = "/"+bucketName + objectName;
//            Banner banner = new Banner();
//            banner.setPic(picUrl);
//            int result = bannerMapper.insert(banner);
//            if (result > 0) {
//                return R.success("新增轮播图成功");
//            } else {
//                return R.error("新增轮播图失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return R.error("上传图片失败");
//        }
        String fileName =picFile.getOriginalFilename();
        MinioUploadController.uploadBannerImgFile(picFile);
        Banner banner = new Banner();
        String path= bucketName+"/img/banner/";
        String imgPath=path+fileName;
        banner.setPic(imgPath);
        if (bannerMapper.insert(banner) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R deleteBanner(Integer id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            return R.error("轮播图不存在");
        }
        int result = bannerMapper.deleteById(id);
        if (result > 0) {
            try {
                String objectName = banner.getPic().substring(banner.getPic().lastIndexOf("/") + 1);
                minioClient.removeObject(io.minio.RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return R.success("删除轮播图成功");
        } else {
            return R.error("删除轮播图失败");
        }
    }

    @Override
    public R updateBannerImg(MultipartFile file, Integer id) {

        String fileName = file.getOriginalFilename();
        MinioUploadController.uploadBannerImgFile(file);
        Banner banner = new Banner();
        String path = bucketName + "/img/banner/";
        String imgPath = path + fileName;
        banner.setId(id);
        banner.setPic(imgPath);
        if (bannerMapper.updateById(banner) > 0) {
            return R.success("更新成功");
        } else {
            return R.error("更新失败");
        }
    }
}