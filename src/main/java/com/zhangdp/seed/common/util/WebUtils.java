package com.zhangdp.seed.common.util;

import com.zhangdp.seed.common.constant.CommonConst;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;

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
    private static final String[] TRUE_PARAMS = {"1", "true", "yes", "y", "ok", "on"};
    /**
     * 默认false的参数列表
     */
    private static final String[] FALSE_PARAMS = {"0", "false", "no", "n", "off"};

    private WebUtils() {

    }

    /**
     * 获取请求url
     *
     * @param request
     * @return
     */
    public static String getBaseUrl(HttpServletRequest request) {
        String url = request.getScheme() + "://" + request.getServerName();
        int port = request.getServerPort();
        // 80和443端口是隐藏的
        if (port != 80 && port != 443) {
            url += ":" + port;
        }
        url += request.getContextPath() + "/";
        return url;
    }

    /**
     * 获取完整请求url
     *
     * @param request
     * @return
     */
    public static String getFullUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        return url + (queryString != null ? ("?" + queryString) : "");
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
    public static boolean isParamMatch(Object param, String[] matches) {
        if (param == null) {
            return false;
        }
        String p = param.toString().trim();
        if (p.length() == 0) {
            return false;
        }
        return Arrays.stream(matches).anyMatch(m -> m.equalsIgnoreCase(p));
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
            if (s.length() == 0) {
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
        return responseJson(response, 200, json);
    }

    /**
     * 输出json
     *
     * @param response
     * @param httpStatus
     * @param json
     * @return
     */
    public static boolean responseJson(HttpServletResponse response, int httpStatus, String json) {
        try {
            response.setStatus(httpStatus);
            response.setCharacterEncoding(CommonConst.CHARSET);
            response.setContentType("application/json");
            response.getWriter().write(json);
            response.flushBuffer();
            return true;
        } catch (IOException e) {
            log.error("response输出json出错, json=" + json, e);
            return false;
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
        int len = -1;
        try {
            String disposition = "attachment";
            if (filename != null && (filename = filename.trim()).length() > 0) {
                disposition += ";filename=\"" + URLEncoder.encode(filename, CommonConst.CHARSET) + "\"";
            }
            response.setHeader("Content-Disposition", disposition);
            response.setCharacterEncoding(CommonConst.CHARSET);
            response.setContentType(contentType != null && (contentType = contentType.trim()).length() > 0 ? contentType : "application/octet-stream");
            response.setContentLengthLong(fileSize);
            ServletOutputStream out = response.getOutputStream();
            byte[] buf = new byte[8192];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error("response输出文件失败, filename=" + filename, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
        }
        return len;
    }

    /**
     * 输出文件
     *
     * @param response
     * @param file
     * @return
     */
    @SneakyThrows
    public static long responseFile(HttpServletResponse response, File file) {
        return responseFile(response, new FileInputStream(file), file.getName(), null, file.length());
    }

}
