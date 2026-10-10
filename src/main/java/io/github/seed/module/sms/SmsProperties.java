package io.github.seed.module.sms;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.sms.data.SmsMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信配置（{@code app.sms}）
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(SmsProperties.CONFIG_PREFIX)
public class SmsProperties {

    public static final String CONFIG_PREFIX = "app.sms";

    /**
     * 兜底模板的配置key，业务场景未单独配置模板时使用
     */
    public static final String DEFAULT_TEMPLATE_KEY = "default";

    /**
     * 短信签名，需与服务商报备的名称一致，单条消息可通过{@link SmsMessage#signName(String)}覆盖
     */
    private String signName;
    /**
     * 业务场景 -> 模板编码，{@link #DEFAULT_TEMPLATE_KEY}为兜底；都未命中时业务方退化为发送文本内容
     */
    private Map<String, String> templates = new LinkedHashMap<>();

    /**
     * 每轮最多派发的条数
     */
    private int sendBatchSize = 100;
    /**
     * 最大重试次数（总发送次数 = 1 + 该值），超过后置为终态失败、不再被调度方领取
     */
    private int maxRetry = 3;
    /**
     * 发送中状态的超时时间（毫秒）：节点发送过程中异常中断时记录会停留在发送中，超过该时间可被重新领取
     */
    private long sendingTimeout = 300000L;

    /**
     * 发送中超时阈值：updated_at早于该时间的发送中记录视为可重新领取
     *
     * @return 超时时间点的 LocalDateTime
     */
    public LocalDateTime sendingStaleBefore() {
        return LocalDateTime.now().minus(Duration.ofMillis(Math.max(this.sendingTimeout, 0L)));
    }

    /**
     * 取场景对应的模板编码，未配置时回退到兜底模板，都没有则返回null
     *
     * @param scene 业务场景
     * @return 模板编码，没有则返回null
     */
    public String getTemplate(String scene) {
        if (this.templates == null || this.templates.isEmpty()) {
            return null;
        }
        String templateCode = this.templates.get(scene);
        if (StrUtil.isBlank(templateCode)) {
            templateCode = this.templates.get(DEFAULT_TEMPLATE_KEY);
        }
        return StrUtil.isBlank(templateCode) ? null : templateCode.trim();
    }

}
