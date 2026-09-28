package io.github.seed.module.sms;

import cn.hutool.v7.core.text.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信配置
 * <br>统一放在{@code app.sms}前缀下：发送器自身的配置（签名、模板）与调度/重试策略配置集中一处，
 * 便于运维一处看全；其中调度与重试相关的项由编排层{@code SmsManager}与调度方{@code SmsSendTask}消费，
 * 发送器本身只用签名与模板
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
     * 业务场景 -> 短信模板编码，key为{@link #DEFAULT_TEMPLATE_KEY}时作为兜底模板；
     * 场景未匹配到模板时，业务方会退化为发送文本内容（需服务商支持）
     */
    private Map<String, String> templates = new LinkedHashMap<>();

    /**
     * 是否启用内置的定时派发任务（轮询待发送记录并调用{@code SmsManager#dispatch}）
     * <br>为false时{@code SmsSendTask}的Bean根本不注册（见{@code TaskConfigurer}），
     * 后续改为消息队列调度时置为false即可，发送入口不变
     */
    private boolean sendEnabled = true;
    /**
     * 定时派发任务的轮询间隔（毫秒），上一轮结束后间隔该时间再发起下一轮
     */
    private long sendPollInterval = 5000L;
    /**
     * 定时派发任务每轮最多派发的短信条数
     */
    private int sendBatchSize = 100;
    /**
     * 发送失败后允许的最大重试次数（总发送次数 = 1 + 该值）
     * <br>每次发送失败失败次数加1，未超过该值时状态退回待发送等待下轮调度重试，
     * 超过后置为发送失败终态、不再被调度方领取
     */
    private int maxRetry = 3;
    /**
     * 发送中状态的超时时间（毫秒）
     * <br>记录被抢占后状态为发送中，若节点在发送过程中异常中断，记录会一直停留在发送中；
     * 超过该时间仍未回写结果的发送中记录，允许被其他节点重新领取
     */
    private long sendingTimeout = 300000L;

    /**
     * 发送中超时阈值：早于该时间的发送中记录视为可重新领取
     *
     * @return
     */
    public LocalDateTime sendingStaleBefore() {
        return LocalDateTime.now().minus(Duration.ofMillis(Math.max(this.sendingTimeout, 0L)));
    }

    /**
     * 取业务场景对应的模板编码，场景未配置时回退到兜底模板，都没有则返回null
     *
     * @param scene 业务场景
     * @return 模板编码，未配置时返回null
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
