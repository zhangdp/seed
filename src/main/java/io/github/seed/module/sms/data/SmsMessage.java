package io.github.seed.module.sms.data;

import cn.hutool.v7.core.text.StrUtil;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信发送入参，支持两种形式：
 * <ol>
 *     <li><b>文本短信</b>：{@link #text(String, String)}，直接把内容作为短信正文发送，
 *     接入简单但国内服务商普遍要求内容报备，一般只在国际短信或测试环境可用</li>
 *     <li><b>模板短信</b>：{@link #template(String, String, Map)}，由服务商按模板编码与参数渲染后发送，
 *     国内服务商基本都要求模板先报备，生产环境推荐使用本形式</li>
 * </ol>
 * 两种形式互斥，不能同时指定内容与模板编码
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
public class SmsMessage {

    /**
     * 手机号
     */
    private final String mobile;
    /**
     * 短信签名，为空时由发送器使用全局配置的签名
     */
    private String signName;
    /**
     * 短信文本内容，模板短信时为空
     */
    private final String content;
    /**
     * 短信模板编码（阿里云TemplateCode、腾讯云TemplateId）
     */
    private final String templateCode;
    /**
     * 短信模板参数，保持插入顺序
     */
    private final Map<String, Object> templateParams;

    private SmsMessage(String mobile, String content, String templateCode, Map<String, Object> templateParams) {
        if (StrUtil.isBlank(mobile)) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (StrUtil.isBlank(content) == StrUtil.isBlank(templateCode)) {
            throw new IllegalArgumentException("短信文本内容与模板编码必须且只能指定一个");
        }
        this.mobile = mobile.trim();
        this.content = content;
        this.templateCode = templateCode;
        // 保持插入顺序：腾讯云等厂商的模板参数是有序占位符{1}{2}，靠顺序与参数对应
        this.templateParams = templateParams == null || templateParams.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(templateParams));
    }

    /**
     * 文本短信
     *
     * @param mobile  手机号
     * @param content 短信正文
     * @return
     */
    public static SmsMessage text(String mobile, String content) {
        return new SmsMessage(mobile, content, null, null);
    }

    /**
     * 模板短信
     *
     * @param mobile         手机号
     * @param templateCode   模板编码
     * @param templateParams 模板参数，可为空
     * @return
     */
    public static SmsMessage template(String mobile, String templateCode, Map<String, Object> templateParams) {
        return new SmsMessage(mobile, null, templateCode, templateParams);
    }

    /**
     * 指定本条消息的短信签名，不指定则使用全局配置的签名
     *
     * @param signName 短信签名
     * @return 当前对象，便于链式调用
     */
    public SmsMessage signName(String signName) {
        this.signName = signName;
        return this;
    }

    /**
     * 是否模板短信
     *
     * @return
     */
    public boolean isTemplate() {
        return this.templateCode != null;
    }

}
