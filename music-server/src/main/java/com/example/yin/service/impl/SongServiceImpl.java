package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.controller.MinioUploadController;
import com.example.yin.mapper.SongMapper;
import com.example.yin.model.domain.Song;
import com.example.yin.model.request.SongRequest;
import com.example.yin.service.SongService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    private SongMapper songMapper;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Autowired
    private MinioClient minioClient;

    @Override
    public R allSong() {
        return R.success(null, songMapper.selectList(null));
    }

    @Override
    public R addSong(SongRequest addSongRequest, MultipartFile lrcFile, MultipartFile mpFile) {
        try {
            Song song = new Song();
            BeanUtils.copyProperties(addSongRequest, song);
            String pic = "/img/songPic/tubiao.jpg";

            // 上传MP3文件
            String fileName = mpFile.getOriginalFilename();
;
            String uploadResult = MinioUploadController.uploadFile(mpFile);
            String storeUrlPath = "/" + bucketName + "/" + fileName;


            // 检查文件是否成功上传到MinIO
            if (!isFileExistsInMinio(fileName)) {
                return R.error("MP3文件未成功上传到MinIO");
            }

            song.setCreateTime(new Date());
            song.setUpdateTime(new Date());
            song.setPic(pic);
            song.setUrl(storeUrlPath);

            if (lrcFile != null && (song.getLyric().equals("[00:00:00]暂无歌词"))) {
                String content = new String(lrcFile.getBytes(), "GB2312");
                song.setLyric(content);
            }

            if (uploadResult.equals("File uploaded successfully!") && songMapper.insert(song) > 0) {
                // 验证URL格式
                if (!isValidUrlFormat(storeUrlPath)) {
                    return R.error("生成的URL格式不正确");
                }
                return R.success("上传成功", storeUrlPath);
            } else {
                return R.error("上传失败");
            }
        } catch (Exception e) {
            return R.error("上传过程中发生异常: " + e.getMessage());
        }
    }

    @Override
    public R updateSongMsg(SongRequest updateSongRequest) {
        Song song = new Song();
        BeanUtils.copyProperties(updateSongRequest, song);
        if (songMapper.updateById(song) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updateSongUrl(MultipartFile urlFile, int id) {
        try {
            Song song = songMapper.selectById(id);
            String oldPath = song.getUrl();
            String[] parts = oldPath.split("/");
            String oldFileName = parts[parts.length - 1];

            // 删除旧文件
            removeFileFromMinio(oldFileName);

            // 上传新文件
            String newFileName = urlFile.getOriginalFilename();
            String uploadResult = MinioUploadController.uploadFile(urlFile);
            String storeUrlPath = "/" + bucketName + "/" + newFileName;

            // 检查新文件是否成功上传
            if (!isFileExistsInMinio(newFileName)) {
                return R.error("新MP3文件未成功上传到MinIO");
            }

            song.setId(id);
            song.setUrl(storeUrlPath);
            song.setName(newFileName);

            if (uploadResult.equals("File uploaded successfully!") && songMapper.updateById(song) > 0) {
                // 验证URL格式
                if (!isValidUrlFormat(storeUrlPath)) {
                    return R.error("生成的URL格式不正确");
                }
                return R.success("更新成功", storeUrlPath);
            } else {
                return R.error("更新失败");
            }
        } catch (Exception e) {
            return R.error("更新过程中发生异常: " + e.getMessage());
        }
    }

    @Override
    public R updateSongPic(MultipartFile urlFile, int id) {
        try {
            String fileName = urlFile.getOriginalFilename();
            String storeUrlPath = "/music/singer/song/" + fileName;
            MinioUploadController.uploadSongImgFile(urlFile);

            // 检查图片文件是否成功上传
            if (!isFileExistsInMinio(fileName)) {
                return R.error("图片文件未成功上传到MinIO");
            }

            Song song = new Song();
            song.setId(id);
            song.setPic(storeUrlPath);

            if (songMapper.updateById(song) > 0) {
                // 验证URL格式
                if (!isValidUrlFormat(storeUrlPath)) {
                    return R.error("生成的URL格式不正确");
                }
                return R.success("上传成功", storeUrlPath);
            } else {
                return R.error("上传失败");
            }
        } catch (Exception e) {
            return R.error("上传过程中发生异常: " + e.getMessage());
        }
    }

    @Override
    public R deleteSong(Integer id) {
        try {
            Song song = songMapper.selectById(id);
            String path = song.getUrl();
            String[] parts = path.split("/");
            String fileName = parts[parts.length - 1];

            if (songMapper.deleteById(id) > 0) {
                removeFileFromMinio(fileName);
                return R.success("删除成功");
            } else {
                return R.error("删除失败");
            }
        } catch (Exception e) {
            return R.error("删除过程中发生异常: " + e.getMessage());
        }
    }

    @Override
    public R songOfSingerId(Integer singerId) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("singer_id", singerId);
        return R.success(null, songMapper.selectList(queryWrapper));
    }

    @Override
    public R songOfId(Integer id) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return R.success(null, songMapper.selectList(queryWrapper));
    }

    @Override
    public R songOfSingerName(String name) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        return R.success(null, songMapper.selectList(queryWrapper));
    }

    @Override
    public R updateSongLrc(MultipartFile lrcFile, int id) {
        try {
            Song song = songMapper.selectById(id);
            if (lrcFile != null && !(song.getLyric().equals("[00:00:00]暂无歌词"))) {
                String content = new String(lrcFile.getBytes(), "GB2312");
                song.setLyric(content);
            }

            if (songMapper.updateById(song) > 0) {
                return R.success("更新成功");
            } else {
                return R.error("更新失败");
            }
        } catch (Exception e) {
            return R.error("更新过程中发生异常: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在于MinIO中
     * @param fileName 文件名
     * @return 如果存在返回true，否则返回false
     */
    private boolean isFileExistsInMinio(String fileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            // 如果文件不存在，MinIO会抛出ErrorResponseException
            if (e instanceof ErrorResponseException && e.getMessage().contains("Object does not exist")) {
                return false;
            }
            // 其他异常情况记录日志并返回false
            System.err.println("检查MinIO文件存在性时发生异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从MinIO中删除文件
     * @param fileName 文件名
     */
    private void removeFileFromMinio(String fileName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            System.err.println("删除MinIO文件时发生异常: " + e.getMessage());
            // 可以选择记录日志或抛出更具体的异常
        }
    }

    /**
     * 验证URL格式是否正确
     * @param url 要验证的URL
     * @return 如果格式正确返回true，否则返回false
     */
    private boolean isValidUrlFormat(String url) {
        // 简单的URL格式验证，确保以/bucket/开头
        String pattern = "^/" + Pattern.quote(bucketName) + "/.+$";
        return Pattern.matches(pattern, url);
    }
}