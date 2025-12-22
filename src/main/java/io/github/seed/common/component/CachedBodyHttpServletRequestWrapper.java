package io.github.seed.common.component;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 2025/12/22 缓存http request请求body内容，方便多次读取
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;
    private volatile boolean cachedFlag = false;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!cachedFlag) {
            this.caching();
        }
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (!cachedFlag) {
            caching();
        }
        String encoding = super.getCharacterEncoding();
        if (encoding == null || encoding.isEmpty()) {
            encoding = StandardCharsets.UTF_8.name();
        }
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.cachedBody), encoding));
    }

    /**
     * 缓存inputStream body内容
     *
     * @throws IOException
     */
    private synchronized void caching() throws IOException {
        // 双重检查，防止并发重复读
        if (cachedFlag) {
            return;
        }
        try (InputStream is = super.getInputStream()) {
            cachedBody = is.readAllBytes();
        }
        cachedFlag = true;
    }

    /**
     * 包装ServletInputStream，实现可重复读
     */
    public static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream caching;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.caching = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return caching.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // no-op (sync request)
        }

        @Override
        public int read() throws IOException {
            return caching.read();
        }
    }
}
