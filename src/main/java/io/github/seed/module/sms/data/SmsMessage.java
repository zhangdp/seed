package io.github.seed.module.sms.data;

import cn.hutool.v7.core.text.StrUtil;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信发送入参，两种形式互斥：文本内容与模板编码必须且只能指定一个
 * <ul>
 *     <li>{@link #text}：内容即正文，接入简单，但国内服务商普遍要求内容报备</li>
 *     <li>{@link #template}：由服务商按模板编码与参数渲染，生产环境推荐</li>
 * </ul>
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
     * 模板参数，保持插入顺序：腾讯云等厂商的模板参数是有序占位符{1}{2}，靠顺序对应
     */
    private final Map<String, Object> templateParams;
    /**
     * 优先级，越大越优先发送，默认{@link SmsPriority#NORMAL}
     */
    private SmsPriority priority = SmsPriority.NORMAL;

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
        this.templateParams = templateParams == null || templateParams.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(templateParams));
    }

    /**
     * 文本短信
     */
    public static SmsMessage text(String mobile, String content) {
        return new SmsMessage(mobile, content, null, null);
    }

    /**
     * 模板短信
     */
    public static SmsMessage template(String mobile, String templateCode, Map<String, Object> templateParams) {
        return new SmsMessage(mobile, null, templateCode, templateParams);
    }

    /**
     * 指定签名，不指定则使用全局配置的签名
     */
    public SmsMessage signName(String signName) {
        this.signName = signName;
        return this;
    }

    /**
     * 指定优先级，不指定则为{@link SmsPriority#NORMAL}
     */
    public SmsMessage priority(SmsPriority priority) {
        if (priority != null) {
            this.priority = priority;
        }
        return this;
    }

    /**
     * 是否模板短信
     */
    public boolean isTemplate() {
        return this.templateCode != null;
    }

}
