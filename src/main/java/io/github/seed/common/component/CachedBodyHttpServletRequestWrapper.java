package io.github.seed.common.component;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 2025/12/22 缓存http request请求body内容，方便多次读取
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;
    private volatile boolean cachedFlag = false;
    private final ReentrantLock lock = new ReentrantLock();

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
    private void caching() throws IOException {
        lock.lock();
        try {
            // 双重检查，防止并发重复读
            if (cachedFlag) {
                return;
            }
            try (InputStream is = super.getInputStream()) {
                cachedBody = is.readAllBytes();
            }
            cachedFlag = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 包装ServletInputStream，实现可重复读
     */
    public static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream byteArrayInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("CachedBodyServletInputStream does not support async read listeners.");
        }

        @Override
        public int read() throws IOException {
            return byteArrayInputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.byteArrayInputStream.read(b, off, len);
        }
    }
}
