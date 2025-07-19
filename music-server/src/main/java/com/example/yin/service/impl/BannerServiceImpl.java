package com.example.yin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.BannerMapper;
import com.example.yin.model.domain.Banner;
import com.example.yin.service.BannerService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Cacheable(value = "banner", key = "'list'")
    @Override
    public List<Banner> getAllBanner() {
        System.out.println("没有走缓存");
        return bannerMapper.selectList(null);
    }

    @Override
    public R addBanner(MultipartFile picFile) {
        if (picFile.isEmpty()) {
            return R.error("图片不能为空");
        }
        try {
            // 构建存储路径，存入 img/banner/ 下，文件名保持不变
            String objectName = "img/banner/" + picFile.getOriginalFilename();
            InputStream inputStream = picFile.getInputStream();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, picFile.getSize(), -1)
                    .contentType(picFile.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);
//            String picUrl = minioClient.getObjectUrl(bucketName, objectName);
            String picUrl = objectName;
            Banner banner = new Banner();
            banner.setPic(picUrl);
            int result = bannerMapper.insert(banner);
            if (result > 0) {
                return R.success("新增轮播图成功");
            } else {
                return R.error("新增轮播图失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传图片失败");
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
        if (file.isEmpty()) {
            return R.error("图片不能为空");
        }
        try {
            // 构建存储路径，存入 img/banner/ 下，文件名保持不变
            String objectName = "img/banner/" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);

            // 获取新的图片 URL
            String picUrl = objectName;

            // 更新数据库中的图片 URL
            Banner banner = new Banner();
            banner.setId(id);
            banner.setPic(picUrl);
            int result = bannerMapper.updateById(banner);

            if (result > 0) {
                return R.success("更新轮播图图片成功");
            } else {
                return R.error("更新轮播图图片失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传图片失败");
        }
    }
}