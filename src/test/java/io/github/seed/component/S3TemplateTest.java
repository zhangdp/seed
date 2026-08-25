package io.github.seed.component;

import cn.hutool.v7.core.date.TimeUtil;
import io.github.seed.common.stoge.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.*;

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
public class S3TemplateTest {

    @Autowired
    private S3Template s3Template;

    @Test
    public void listObjects() {
        String path = "upload2/";
        ListObjectsV2Response res = s3Template.listObjects(path, 100);
        log.debug("列出子文件列表，path={}，result={}", path, res);
    }

    @Test
    public void download() throws IOException {
        String path = "upload/2024/11/18/1858417482725986304.txt";
        String localPath = "/Users/peng/tmp/1858417482725986304.txt";
        long result = s3Template.download(path, localPath);
        log.debug("下载文件{}到{}：{}", path, localPath, result);
    }

    @Test
    public void upload() throws IOException {
        String dir = "upload/" + TimeUtil.format(LocalDate.now(), "yyyy/MM/dd/");
        String localPath = "/Users/peng/tmp/智能小飞切换服务器地址为https加域名.docx";
        String fileName = localPath.substring(localPath.lastIndexOf("/") + 1);
        String path = dir + fileName;
        PutObjectResponse result = s3Template.upload(path, localPath);
        log.debug("上传文件{}到{}：{}", localPath, path, result);
    }

    @Test
    public void delete() {
        String path = "upload/2024/11/18/9594905148383235.docx";
        DeleteObjectResponse result = s3Template.delete(path);
        log.debug("删除文件{}：{}", path, result);
    }

    @Test
    public void copy() {
        String srcPath = "upload/2024/11/18/1858418349910921216.jpg";
        String destPath = "upload/2026/07/21/1858418349910921216.jpg";
        CopyObjectResponse result = s3Template.copy(srcPath, destPath);
        log.debug("复制文件{}到{}：{}", srcPath, destPath, result);
    }

    @Test
    public void move() {
        String srcPath = "upload/2024/11/18/1858427159597682688.html";
        String destPath = "upload/2026/07/21/1858427159597682688.html";
        CopyObjectResponse result = s3Template.move(srcPath, destPath);
        log.debug("移动文件{}到{}：{}", srcPath, destPath, result);
    }

    @Test
    public void headObject() {
        String path = "upload/2024/11/18/1858417482725986304.txt";
        HeadObjectResponse result = s3Template.headObject(path);
        log.debug("获取文件信息，path={}, result={}", path, result);
    }
}
