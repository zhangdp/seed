package io.github.seed.common.component;

import cn.hutool.v7.core.io.file.FileUtil;
import io.github.seed.common.data.StogeData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author zhangdp
 * @since 2026/8/3
 */
@AllArgsConstructor
@Getter
@Setter
public class LocalStogeAdapter implements StogeAdapter {

    @Override
    public StogeData upload(String path, MultipartFile file) {
        return null;
    }

    @Override
    public StogeData upload(String path, File file) {
        FileUtil.copy(file, new File(path), true);
        return null;
    }

    @Override
    public InputStream download(String path) {
        return null;
    }

    @Override
    public boolean delete(String path) {
        return false;
    }

    @Override
    public List<String> deleteBatch(String... paths) {
        return List.of();
    }

    @Override
    public List<String> deleteBatch(Collection<String> paths) {
        return List.of();
    }
}
