package io.github.seed.module.captcha.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 行为验证码的用户操作轨迹，字段名与tianai官方前端组件采集的JSON一致，前端无需适配
 * <br>校验依赖轨迹本身：滑动类取首尾点算位移，文字点选取{@code type=click}的点坐标
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "行为验证码操作轨迹")
public class BehaviorCaptchaTrack implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 背景图宽度
     */
    @Schema(title = "背景图宽度")
    private Integer bgImageWidth;
    /**
     * 背景图高度
     */
    @Schema(title = "背景图高度")
    private Integer bgImageHeight;
    /**
     * 模板图宽度
     */
    @Schema(title = "模板图宽度")
    private Integer templateImageWidth;
    /**
     * 模板图高度
     */
    @Schema(title = "模板图高度")
    private Integer templateImageHeight;
    /**
     * 开始操作时间
     */
    @Schema(title = "开始操作时间")
    private Long startTime;
    /**
     * 结束操作时间
     */
    @Schema(title = "结束操作时间")
    private Long stopTime;
    /**
     * 滑块相对背景图的left偏移
     */
    @Schema(title = "滑块left偏移")
    private Integer left;
    /**
     * 滑块相对背景图的top偏移
     */
    @Schema(title = "滑块top偏移")
    private Integer top;
    /**
     * 轨迹点序列
     */
    @Schema(title = "轨迹点序列")
    private List<Point> trackList;
    /**
     * 附加数据
     */
    @Schema(title = "附加数据")
    private Object data;

    /**
     * 轨迹点：坐标与相对时间
     */
    @Data
    @Schema(title = "轨迹点")
    public static class Point implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 横坐标
         */
        @Schema(title = "横坐标")
        private Float x;
        /**
         * 纵坐标
         */
        @Schema(title = "纵坐标")
        private Float y;
        /**
         * 相对开始时间的时间差
         */
        @Schema(title = "时间差")
        private Float t;
        /**
         * 点位类型，文字点选时为click表示点击
         */
        @Schema(title = "点位类型", description = "文字点选时为click表示点击")
        private String type;
    }

}
