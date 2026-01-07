package io.github.seed.common.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 2023/4/4 web相关工具
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class WebUtils {

    /**
     * ip header
     */
    private static final String[] IP_HEADERS = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
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
    private static final Charset DEFAULT_CHARSET = UTF_8;

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
        if (url.contains("?")) {
            if (!url.endsWith("&")) {
                sb.append('&');
            }
        } else if (!url.endsWith("?")) {
            sb.append('?');
        }
        sb.append(name.trim());
        sb.append('=');
        if (value != null) {
            sb.append(urlEncode(value.toString()));
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
        char lastChar = 0;
        for (Object o : seg) {
            if (o == null) {
                continue;
            }
            String str = o.toString().trim();
            if (str.isEmpty()) {
                continue;
            }
            // 每段之间添加/
            if (!sb.isEmpty() && lastChar != '/') {
                lastChar = '/';
                sb.append(lastChar);
            }
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '\\') {
                    c = '/';
                }
                // 如果已经有/了就跳过保证只有单个/
                if (c == '/' && lastChar == '/') {
                    continue;
                }
                lastChar = c;
                sb.append(lastChar);
            }
        }
        return sb.toString();
    }

    /**
     * 清空session值
     *
     * @param session
     */
    public static void clearSession(HttpSession session) {
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            session.removeAttribute(names.nextElement());
        }
    }

    /**
     * 响应json
     *
     * @param response
     * @param json
     * @return
     */
    public static boolean responseJson(HttpServletResponse response, String json) {
        return responseJson(response, json, null);
    }

    /**
     * 响应json
     *
     * @param response
     * @param json
     * @param charset
     * @return
     */
    public static boolean responseJson(HttpServletResponse response, String json, String charset) {
        try {
            if (charset != null && !charset.isEmpty()) {
                response.setCharacterEncoding(charset);
            }
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            return true;
        } catch (IOException e) {
            log.error("response响应json出错, json={}", json, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }

    /**
     * 响应附件下载的头部信息
     *
     * @param response
     * @param fileName
     * @param fileSize
     */
    public static void responseDispositionHeader(HttpServletResponse response, String fileName, long fileSize) {
        responseDispositionHeader(response, fileName, null, fileSize, false);
    }

    /**
     * 响应附件下载的头部信息
     *
     * @param response
     * @param fileName
     * @param fileSize
     * @param contentType
     */
    public static void responseDispositionHeader(HttpServletResponse response, String fileName, long fileSize, String contentType) {
        responseDispositionHeader(response, fileName, contentType, fileSize, false);
    }

    /**
     * 响应附件下载的头部信息
     *
     * @param response
     * @param fileName
     * @param fileSize
     * @param isInline
     */
    public static void responseDispositionHeader(HttpServletResponse response, String fileName, long fileSize, boolean isInline) {
        responseDispositionHeader(response, fileName, null, fileSize, isInline);
    }

    /**
     * 响应附件下载的头部信息
     *
     * @param response
     * @param fileName
     * @param contentType
     * @param fileSize
     * @param isInline
     */
    public static void responseDispositionHeader(HttpServletResponse response, String fileName, String contentType, long fileSize, boolean isInline) {
        String disposition = isInline ? "inline" : "attachment";
        if (fileName != null && !fileName.isEmpty()) {
            String encodedFilename = urlEncode(fileName);
            disposition += "; filename=\"" + encodedFilename + "\"; filename*=" + DEFAULT_CHARSET.name() + "''" + encodedFilename;
        }
        response.setHeader("Content-Disposition", disposition);
        response.setContentType(contentType == null || contentType.isEmpty() ? "application/octet-stream" : contentType);
        if (fileSize > 0L) {
            response.setContentLengthLong(fileSize);
        }
    }

    /**
     * 响应文件
     *
     * @param response
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static long responseFile(HttpServletResponse response, File file) throws FileNotFoundException {
        return responseFile(response, file, null, false);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param file
     * @param isInline
     * @return
     * @throws FileNotFoundException
     */
    public static long responseFile(HttpServletResponse response, File file, boolean isInline) throws FileNotFoundException {
        return responseFile(response, file, null, isInline);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param file
     * @param contentType
     * @return
     * @throws FileNotFoundException
     */
    public static long responseFile(HttpServletResponse response, File file, String contentType) throws FileNotFoundException {
        return responseFile(response, file, contentType, false);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param file
     * @param contentType
     * @param isInline
     * @return
     */
    public static long responseFile(HttpServletResponse response, File file, String contentType, boolean isInline) throws FileNotFoundException {
        return responseFile(response, new FileInputStream(file), file.getName(), file.length(), contentType, isInline);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static long responseFile(HttpServletResponse response, String path) throws FileNotFoundException {
        return responseFile(response, path, null, false);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param path
     * @param contentType
     * @return
     * @throws FileNotFoundException
     */
    public static long responseFile(HttpServletResponse response, String path, String contentType) throws FileNotFoundException {
        return responseFile(response, path, contentType, false);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param path
     * @param isInline
     * @return
     * @throws FileNotFoundException
     */
    public static long responseFile(HttpServletResponse response, String path, boolean isInline) throws FileNotFoundException {
        return responseFile(response, path, null, isInline);
    }

    /**
     * 响应文件
     *
     * @param response
     * @param path
     * @param contentType
     * @param isInline
     * @return
     */
    public static long responseFile(HttpServletResponse response, String path, String contentType, boolean isInline) throws FileNotFoundException {
        return responseFile(response, new File(path), contentType, isInline);
    }

    /**
     * 响应文件流
     *
     * @param response
     * @param in
     * @param fileName
     * @param fileSize
     * @return
     */
    public static long responseFile(HttpServletResponse response, InputStream in, String fileName, long fileSize) {
        return responseFile(response, in, fileName, fileSize, null, false);
    }

    /**
     * 响应文件流。会自动关闭输入流
     *
     * @param response
     * @param in
     * @param fileName
     * @param fileSize
     * @param isInline
     * @return
     */
    public static long responseFile(HttpServletResponse response, InputStream in, String fileName, long fileSize, boolean isInline) {
        return responseFile(response, in, fileName, fileSize, null, isInline);
    }

    /**
     * 响应文件流。会自动关闭输入流
     *
     * @param response
     * @param in
     * @param fileName
     * @param fileSize
     * @param contentType
     * @return
     */
    public static long responseFile(HttpServletResponse response, InputStream in, String fileName, long fileSize, String contentType) {
        return responseFile(response, in, fileName, fileSize, contentType, false);
    }

    /**
     * 响应文件流。会自动关闭输入流
     *
     * @param response
     * @param in
     * @param fileName
     * @param fileSize
     * @param contentType
     * @param isInline
     * @return
     */
    public static long responseFile(HttpServletResponse response, InputStream in, String fileName, long fileSize, String contentType, boolean isInline) {
        try (in) {
            responseDispositionHeader(response, fileName, contentType, fileSize, isInline);
            OutputStream out = response.getOutputStream();
            long size = in.transferTo(out);
            out.flush();
            return size;
        } catch (IOException e) {
            log.error("response响应文件失败, fileName={}", fileName, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return -1;
        }
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


    /**
     * 输出EventStream data类型数据
     *
     * @param response
     * @param strData
     */
    public static void responseEventStreamData(HttpServletResponse response, String strData) throws IOException {
        responseEventStream(response, "data", strData);
    }

    /**
     * 输出EventStream数据
     *
     * @param response
     * @param type
     * @param strData
     */
    public static void responseEventStream(HttpServletResponse response, String type, String strData) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write(type + ":" + strData + "\n\n");
        writer.flush();
    }

    /**
     * 设置EventStream头
     *
     * @param response
     */
    public static void setEventStreamHeader(HttpServletResponse response) {
        setEventStreamHeader(response, null);
    }

    /**
     * 设置EventStream头
     *
     * @param response
     * @param charset
     */
    public static void setEventStreamHeader(HttpServletResponse response, String charset) {
        response.setContentType("text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setCharacterEncoding(charset);
    }

    /**
     * url encode，默认使用utf-8编码
     *
     * @param data
     * @return
     */
    public static String urlEncode(String data) {
        return urlEncode(data, DEFAULT_CHARSET);
    }

    /**
     * url encode
     *
     * @param data
     * @param charset
     * @return
     */
    public static String urlEncode(String data, Charset charset) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        // java使用基于 application/x-www-form-urlencoded 编码规则，会将空格编码为+，因此需要替换成20%，详见https://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4
        return URLEncoder.encode(data, charset).replace("+", "%20");
    }

    /**
     * url decode，默认使用utf-8编码
     *
     * @param data
     * @return
     */
    public static String urlDecode(String data) {
        return urlDecode(data, DEFAULT_CHARSET);
    }

    /**
     * url decode
     *
     * @param data
     * @param charset
     * @return
     */
    public static String urlDecode(String data, Charset charset) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        return URLDecoder.decode(data, charset);
    }

    /**
     * url encode
     * 会保留url中的结构字符：`:`, `/`, `?`, `&`, `#`
     *
     * @param str
     * @return
     */
    public static String urlEncodeComponent(String str) {
        String encoded = urlEncode(str);
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }
        // 还原 `:`, `/`, `?`, `&`, `#`
        encoded = encoded.replace("%3A", ":")
                .replace("%2F", "/")
                .replace("%3F", "?")
                .replace("%26", "&")
                .replace("%3D", "=")
                .replace("%23", "#");
        return encoded;
    }

    /**
     * 获取客户端ip
     *
     * @param request
     * @return
     */
    public static String getClientIP(HttpServletRequest request) {
        return getClientIP(request, IP_HEADERS);
    }

    /**
     * 获取客户端ip
     *
     * @param request
     * @param headers
     * @return
     */
    public static String getClientIP(HttpServletRequest request, String... headers) {
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 多级代理，第一个才是真实 IP
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取请求体
     *
     * @param request
     * @return
     */
    public static String getBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}
