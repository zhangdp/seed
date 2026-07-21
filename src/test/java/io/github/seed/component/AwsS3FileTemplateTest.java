package io.github.seed.component;

import cn.hutool.v7.core.date.TimeUtil;
import io.github.seed.common.component.AwsS3FileTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.io.IOException;
import java.time.LocalDate;

/**
 * awss3测试
 *
 * @author zhangdp
 * @since 2026/7/21
 */
@Slf4j
@SpringBootTest
public class AwsS3FileTemplateTest {

    @Autowired
    private AwsS3FileTemplate awsS3FileTemplate;

    @Test
    public void listObjects() {
        String path = "upload/";
        ListObjectsV2Response res = awsS3FileTemplate.listObjects(path, 100);
        log.debug("列出子文件列表，path={}，result={}", path, res);
    }

    @Test
    public void download() throws IOException {
        String path = "upload/2024/11/18/1858417482725986304.txt";
        String localPath = "/Users/peng/tmp/1858417482725986304.txt";
        long result = awsS3FileTemplate.download(path, localPath);
        log.debug("下载文件{}到{}：{}", path, localPath, result);
    }

    @Test
    public void upload() throws IOException {
        String dir = "upload/" + TimeUtil.format(LocalDate.now(), "yyyy/MM/dd/");
        String localPath = "/Users/peng/tmp/智能小飞切换服务器地址为https加域名.docx";
        String fileName = localPath.substring(localPath.lastIndexOf("/") + 1);
        String path = dir + fileName;
        boolean result = awsS3FileTemplate.upload(localPath, path);
        log.debug("上传文件{}到{}：{}", localPath, path, result);
    }

    @Test
    public void delete() {
        String path = "upload/2024/11/18/1858417482725986304.txt";
        boolean result = awsS3FileTemplate.delete(path);
        log.debug("删除文件{}：{}", path, result);
    }

    @Test
    public void copy() {
        String srcPath = "upload/2024/11/18/1858418349910921216.jpg";
        String destPath = "upload/2026/07/21/1858418349910921216.jpg";
        boolean result = awsS3FileTemplate.copy(srcPath, destPath);
        log.debug("复制文件{}到{}：{}", srcPath, destPath, result);
    }

    @Test
    public void move() {
        String srcPath = "upload/2024/11/18/1858427159597682688.html";
        String destPath = "upload/2026/07/21/1858427159597682688.html";
        boolean result = awsS3FileTemplate.move(srcPath, destPath);
        log.debug("移动文件{}到{}：{}", srcPath, destPath, result);
    }
}
