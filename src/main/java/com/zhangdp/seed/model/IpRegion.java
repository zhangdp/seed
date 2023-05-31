package com.zhangdp.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/5/30
 *
 * @author zhangdp
 * @since
 */
@Data
@Accessors(chain = true)
@Schema(title = "IP区域信息")
public class IpRegion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 国家
     */
    @Schema(title = "国家")
    private String country;
    /**
     * 区域
     */
    @Schema(title = "区域")
    private String region;
    /**
     * 省份
     */
    @Schema(title = "省份")
    private String province;
    /**
     * 城市
     */
    @Schema(title = "城市")
    private String city;
    /**
     * 运营商
     */
    @Schema(title = "运营商")
    private String isp;

    /**
     * 简单位置信息
     *
     * @return 如果是中国直辖市则返回城市名；如果是中国非直辖市则返回省份+城市；如果是国外则返回国家名
     */
    public String location() {
        if ("中国".equals(this.country)) {
            if ("北京".equals(this.province) || "重庆".equals(this.province) || "上海".equals(this.province)
                    || "天津".equals(this.province)) {
                return this.city;
            }
            return this.province + (this.city != null ? this.city : "");
        } else {
            return this.country;
        }
    }
}
