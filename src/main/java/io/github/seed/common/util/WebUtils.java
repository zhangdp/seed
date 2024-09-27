package io.github.seed.common.util;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2023/4/4 web相关工具
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class WebUtils {

    /**
     * 默认true参数列表
     */
    private static final String[] TRUE_PARAMS = {"1", "true", "yes", "y", "on"};
    /**
     * 默认false的参数列表
     */
    private static final String[] FALSE_PARAMS = {"0", "false", "no", "n", "off"};
    /**
     * User-Agent 版本号正则
     */
    private static final String UA_VERSION_REGEX = "/([^\\s;]+)(?: \\(([^\\)]+)\\))?";
    /**
     * 默认编码
     */
    private static final String CHARSET = "UTF-8";

    private WebUtils() {

    }

    /**
     * 判断参数是否是true([1, "1", true, "true", "yes", "y", "ok", "on"])，不区分大小写
     *
     * @param param
     * @return
     */
    public static boolean isParamTrue(Object param) {
        return isParamMatch(param, TRUE_PARAMS);
    }

    /**
     * 判断参数否是是false([0, "0", false, "false", "no", "n", "off"])，不区分大小写
     *
     * @param param
     * @return
     */
    public static boolean isParamFalse(Object param) {
        return isParamMatch(param, FALSE_PARAMS);
    }

    /**
     * 参数是否匹配给定的范围内，不区分大小写
     *
     * @param param
     * @param matches
     * @return
     */
    public static boolean isParamMatch(Object param, String... matches) {
        if (param == null) {
            return false;
        }
        String p = param.toString().trim();
        if (p.isEmpty()) {
            return false;
        }
        return Arrays.stream(matches).anyMatch(m -> m.equalsIgnoreCase(p));
    }

    /**
     * url追加参数
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String appendParameter(String url, String name, Object value) {
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf('?') > -1) {
            sb.append('&');
        } else {
            sb.append('?');
        }
        sb.append(name);
        sb.append('=');
        if (value != null) {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 组装成路径
     *
     * @param seg
     * @return
     */
    public static String generatePath(Object... seg) {
        if (seg == null || seg.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : seg) {
            if (o == null) {
                continue;
            }
            String s = o.toString().trim();
            if (s.isEmpty()) {
                continue;
            }
            sb.append('/');
            sb.append(s);
        }
        return sb.toString().replaceAll("/{2,}", "/");
    }

    /**
     * 清空session值
     *
     * @param session
     */
    @Deprecated
    public static void clearSession(HttpSession session) {
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            session.removeAttribute(names.nextElement());
        }
    }

    /**
     * 输出json
     *
     * @param response
     * @param json
     * @return
     */
    public static boolean responseJson(HttpServletResponse response, String json) {
        return responseJson(response, null, json);
    }

    /**
     * 输出json
     *
     * @param response
     * @param httpStatus
     * @param json
     * @return
     */
    public static boolean responseJson(HttpServletResponse response, Integer httpStatus, String json) {
        try {
            if (httpStatus != null) {
                response.setStatus(httpStatus);
            }
            response.setCharacterEncoding(CHARSET);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            return true;
        } catch (IOException e) {
            log.error("response输出json出错, json={}", json, e);
            return false;
        }
    }

    /**
     * 响应附件下载的头部信息
     *
     * @param response
     * @param filename
     * @param contentType
     * @param fileSize
     */
    @SneakyThrows
    public static void responseAttachmentHeader(HttpServletResponse response, String filename, String contentType, long fileSize) {
        String disposition = "attachment";
        if (filename != null && !filename.isEmpty()) {
            // 对文件名进行urlEncode编码。java使用的标准略有不同，会将空格编码为+，因此需要替换成20%，详见https://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4
            String encodedFilename = URLEncoder.encode(filename, CHARSET).replaceAll("\\+", "%20");
            disposition += "; filename=\"" + encodedFilename + "\"; filename*=" + CHARSET + "''" + encodedFilename;
        }
        response.setHeader("Content-Disposition", disposition);
        response.setContentType(contentType == null || contentType.isEmpty() ? "application/octet-stream" : contentType);
        if (fileSize > 0L) {
            response.setContentLengthLong(fileSize);
        }
    }

    /**
     * 输出文件流。会自动关闭输入流
     *
     * @param response
     * @param in
     * @param filename
     * @param contentType
     * @param fileSize
     * @return
     */
    public static long responseFile(HttpServletResponse response, InputStream in, String filename, String contentType, long fileSize) {
        OutputStream out = null;
        try {
            responseAttachmentHeader(response, filename, contentType, fileSize);
            out = response.getOutputStream();
            int total = 0;
            int len;
            byte[] buf = new byte[8192];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
                total += len;
            }
            response.flushBuffer();
            return total;
        } catch (IOException e) {
            log.error("response输出文件失败, filename={}", filename, e);
            return -1;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 输出文件
     *
     * @param response
     * @param file
     * @param contentType
     * @return
     */
    public static long responseFile(HttpServletResponse response, File file, String contentType) throws FileNotFoundException {
        return responseFile(response, new FileInputStream(file), file.getName(), contentType, file.length());
    }

    /**
     * 输出文件
     *
     * @param response
     * @param path
     * @param contentType
     * @return
     */
    public static long responseFile(HttpServletResponse response, String path, String contentType) throws FileNotFoundException {
        return responseFile(response, new File(path), contentType);
    }

    /**
     * 从user-agent中获取指定关键词的版本，如Chrome/127.0.6533.73->[127.0.6533.73, null]、Mozilla/5.0 (Windows NT 6.1; WOW64)->[5.0, Windows NT 6.1; WOW64]
     *
     * @param userAgent
     * @param keyword
     * @return
     */
    public static String[] resolveUAVersion(String userAgent, String keyword) {
        Pattern pattern = Pattern.compile(Pattern.quote(keyword) + UA_VERSION_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        }
        return new String[]{null, null};
    }

}