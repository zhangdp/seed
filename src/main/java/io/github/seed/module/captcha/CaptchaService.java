package io.github.seed.module.captcha;

import cn.hutool.v7.core.text.StrUtil;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.module.captcha.component.CaptchaGenerator;
import io.github.seed.module.captcha.data.BehaviorCaptcha;
import io.github.seed.module.captcha.data.BehaviorCaptchaTrack;
import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.captcha.data.CaptchaTicket;
import io.github.seed.module.captcha.data.CaptchaType;
import io.github.seed.module.captcha.data.ImageCaptcha;
import io.github.seed.module.captcha.store.CaptchaStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 验证码服务，模块对外的唯一入口：行为验证码（滑块/旋转/拼接/文字点选）与短信验证码
 * <br>行为验证码统一出入口：generateBehavior生成、checkBehavior校验轨迹并签发一次性票据，
 * 不同类型对调用方完全透明；票据与短信码走同一个{@link #verify}，
 * 因此业务接口上的{@code @VerifyCaptcha}无需区分验证码形态
 * <br>短信码只生成不投递，交给{@code CaptchaManager}编排
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaService {

    /**
     * 一次性票据码位数
     */
    private static final int TICKET_CODE_LENGTH = 6;

    private final CaptchaProperties captchaProperties;
    private final CaptchaStore captchaStore;
    private final CaptchaGenerator captchaGenerator;
    private final ImageCaptchaApplication imageCaptchaApplication;
    /**
     * 类型随机用，与SecureRandom相比这里只影响选图，Random足够
     */
    private final SecureRandom random = new SecureRandom();

    /**
     * 生成行为验证码，不传类型时从启用的类型中随机；返回的图片与类型交给前端渲染对应的交互组件
     *
     * @param type 验证码类型（SLIDER/ROTATE/CONCAT/WORD_IMAGE_CLICK），为空随机
     * @return 行为验证码（id、类型、背景图与模板图base64、尺寸）
     */
    public BehaviorCaptcha generateBehavior(String type) {
        Set<String> types = captchaProperties.getBehavior().getTypes();
        if (StrUtil.isBlank(type)) {
            List<String> candidates = List.copyOf(types);
            type = candidates.get(random.nextInt(candidates.size()));
        } else if (!types.contains(type)) {
            throw new BizException(ErrorCode.CAPTCHA_TYPE_INVALID.code(),
                    ErrorCode.CAPTCHA_TYPE_INVALID.message() + "：" + type);
        }
        ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(type);
        if (!response.isSuccess() || response.getData() == null) {
            log.error("行为验证码生成失败：type={}, code={}, msg={}", type, response.getCode(), response.getMsg());
            throw new BizException(ErrorCode.CAPTCHA_SEND_FAILED.code(), "验证码生成失败，请稍后再试");
        }
        return this.toBehaviorCaptcha(response.getData());
    }

    /**
     * 校验行为验证码轨迹：校验是一次性消费——无论成败，服务端的校验数据即被移除，
     * 失败必须重新获取验证码；成功后签发一次性票据，业务接口凭票据的key与code走
     * {@code @VerifyCaptcha}切面校验（{@code type=CaptchaType.BEHAVIOR}）
     *
     * @param scene 场景，必须与业务接口{@code @VerifyCaptcha(scene=...)}一致，为空使用默认场景
     * @param id    验证码id
     * @param track 用户操作轨迹
     * @return 一次性票据
     */
    public CaptchaTicket checkBehavior(String scene, String id, BehaviorCaptchaTrack track) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        ApiResponse<?> response = imageCaptchaApplication.matching(id, this.toTianaiTrack(track));
        if (!response.isSuccess()) {
            throw new BizException(ErrorCode.CAPTCHA_INCORRECT.code(),
                    StrUtil.defaultIfBlank(response.getMsg(), ErrorCode.CAPTCHA_INCORRECT.message()));
        }
        // 票据存进通用验证码存储，复用@VerifyCaptcha切面的既有校验与消费逻辑
        String key = captchaGenerator.generateKey();
        String code = captchaGenerator.generateCode(TICKET_CODE_LENGTH);
        captchaStore.saveCode(CaptchaType.BEHAVIOR, s, key, code, captchaProperties.getBehavior().getTicketExpire());
        return new CaptchaTicket(key, code);
    }

    /**
     * 生成图片验证码并缓存
     *
     * @param scene  场景，如login，为空使用默认场景
     * @param width  图片宽度，为空使用配置值
     * @param height 图片高度，为空使用配置值
     * @return 图片验证码
     * @deprecated 字符型图片验证码易被OCR识别，改用{@link #generateBehavior}
     */
    @Deprecated
    public ImageCaptcha generateImage(String scene, Integer width, Integer height) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        CaptchaProperties.Image image = captchaProperties.getImage();
        int w = this.clamp(width, image.getWidth(), image.getMinWidth(), image.getMaxWidth());
        int h = this.clamp(height, image.getHeight(), image.getMinHeight(), image.getMaxHeight());
        CaptchaGenerator.GeneratedImage generated = captchaGenerator.generateImage(w, h,
                image.getCodeLength(), image.getLineCount());
        String key = captchaGenerator.generateKey();
        captchaStore.saveCode(CaptchaType.IMAGE, s, key, generated.code(), image.getExpire());
        ImageCaptcha imageCaptcha = new ImageCaptcha();
        imageCaptcha.setKey(key);
        imageCaptcha.setImage(generated.image());
        return imageCaptcha;
    }

    /**
     * 生成短信验证码：校验场景后生成并缓存，<b>不涉及任何投递动作</b>
     * <br>发送频率与配额由{@code sms}模块在投递前把关，本方法只保证验证码本身可用
     *
     * @param scene  场景，如login、reset_password，为空使用默认场景
     * @param mobile 手机号
     * @return 本次生成的验证码
     */
    public String generateSmsCode(String scene, String mobile) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        // 场景白名单校验，防止前端轮换场景绕过发送频率限制
        Set<String> scenes = captchaProperties.getSms().getScenes();
        if (scenes != null && !scenes.isEmpty() && !scenes.contains(s)) {
            throw new BizException(ErrorCode.CAPTCHA_SCENE_INVALID.code(),
                    ErrorCode.CAPTCHA_SCENE_INVALID.message() + "：" + s);
        }
        CaptchaProperties.Sms sms = captchaProperties.getSms();
        String code = captchaGenerator.generateCode(sms.getCodeLength());
        captchaStore.saveCode(CaptchaType.SMS, s, mobile, code, sms.getExpire());
        return code;
    }

    /**
     * 校验验证码，失败或验证码不存在时抛出{@link BizException}
     *
     * @param type            验证码类型
     * @param scene           场景
     * @param key             验证码key
     * @param code            待校验的验证码
     * @param failCount       允许失败次数，达到即作废验证码
     * @param removeOnSuccess 校验成功后是否删除验证码
     * @param message         自定义错误提示，为空使用默认提示
     */
    public void verify(CaptchaType type, String scene, String key, String code,
                       int failCount, boolean removeOnSuccess, String message) {
        String cacheCode = captchaStore.getCode(type, scene, key);
        if (StrUtil.isBlank(cacheCode)) {
            throw new BizException(ErrorCode.CAPTCHA_EXPIRED.code(),
                    StrUtil.defaultIfBlank(message, ErrorCode.CAPTCHA_EXPIRED.message()));
        }
        if (!StrUtil.equalsIgnoreCase(cacheCode, code)) {
            this.handleVerifyFail(type, scene, key, failCount);
            throw new BizException(ErrorCode.CAPTCHA_INCORRECT.code(),
                    StrUtil.defaultIfBlank(message, ErrorCode.CAPTCHA_INCORRECT.message()));
        }
        // 校验通过，清掉失败计数；验证码默认按一次性消费处理
        captchaStore.deleteFail(type, scene, key);
        if (removeOnSuccess) {
            captchaStore.deleteCode(type, scene, key);
        }
    }

    /**
     * 处理校验失败：累计失败次数，达到上限后作废验证码
     *
     * @param type         验证码类型
     * @param scene        场景
     * @param key          验证码key
     * @param maxFailCount 允许失败次数，达到即作废验证码
     */
    private void handleVerifyFail(CaptchaType type, String scene, String key, int maxFailCount) {
        if (maxFailCount <= 1) {
            // 默认策略：失败一次即作废，两条DEL即可完成
            captchaStore.deleteCode(type, scene, key);
            captchaStore.deleteFail(type, scene, key);
            return;
        }
        Duration ttl = captchaStore.getCodeExpire(type, scene, key);
        if (captchaStore.increaseFail(type, scene, key, ttl) >= maxFailCount) {
            captchaStore.deleteCode(type, scene, key);
            captchaStore.deleteFail(type, scene, key);
        }
    }

    /**
     * 把值夹取到指定范围内，值非法时使用默认值
     *
     * @param value        待夹取的值
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @return 夹取后的值
     */
    private int clamp(Integer value, int defaultValue, int min, int max) {
        int v = value == null || value <= 0 ? defaultValue : value;
        return Math.min(Math.max(v, min), max);
    }

    /**
     * 把tianai的验证码转成模块自己的数据对象，避免第三方类型外泄到controller与前端契约里
     *
     * @param vo tianai验证码
     * @return 行为验证码
     */
    private BehaviorCaptcha toBehaviorCaptcha(ImageCaptchaVO vo) {
        BehaviorCaptcha captcha = new BehaviorCaptcha();
        captcha.setId(vo.getId());
        captcha.setType(vo.getType());
        captcha.setBackgroundImage(vo.getBackgroundImage());
        captcha.setTemplateImage(vo.getTemplateImage());
        captcha.setBackgroundImageWidth(vo.getBackgroundImageWidth());
        captcha.setBackgroundImageHeight(vo.getBackgroundImageHeight());
        captcha.setTemplateImageWidth(vo.getTemplateImageWidth());
        captcha.setTemplateImageHeight(vo.getTemplateImageHeight());
        captcha.setData(vo.getData());
        return captcha;
    }

    /**
     * 把模块自己的轨迹转成tianai的轨迹：两者字段名一致，这里是纯粹的逐点搬运
     *
     * @param track 模块轨迹
     * @return tianai轨迹
     */
    private ImageCaptchaTrack toTianaiTrack(BehaviorCaptchaTrack track) {
        ImageCaptchaTrack tianaiTrack = new ImageCaptchaTrack();
        if (track == null) {
            return tianaiTrack;
        }
        tianaiTrack.setBgImageWidth(track.getBgImageWidth());
        tianaiTrack.setBgImageHeight(track.getBgImageHeight());
        tianaiTrack.setTemplateImageWidth(track.getTemplateImageWidth());
        tianaiTrack.setTemplateImageHeight(track.getTemplateImageHeight());
        tianaiTrack.setStartTime(track.getStartTime());
        tianaiTrack.setStopTime(track.getStopTime());
        tianaiTrack.setLeft(track.getLeft());
        tianaiTrack.setTop(track.getTop());
        tianaiTrack.setData(track.getData());
        List<BehaviorCaptchaTrack.Point> points = track.getTrackList();
        if (points != null) {
            List<ImageCaptchaTrack.Track> tianaiPoints = new ArrayList<>(points.size());
            for (BehaviorCaptchaTrack.Point point : points) {
                tianaiPoints.add(new ImageCaptchaTrack.Track(point.getX(), point.getY(), point.getT(),
                        point.getType()));
            }
            tianaiTrack.setTrackList(tianaiPoints);
        }
        return tianaiTrack;
    }

}
