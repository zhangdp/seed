package com.zhangdp.seed.common.component;

import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.model.IpRegion;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.resource.ResourceUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 2023/5/30 Ip2Region Searcher封装
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class Ip2RegionSearcher implements InitializingBean, DisposableBean {

    private static final String SPLIT_REGEX = "\\|";
    private static final String NOT_VALUE = "0";

    private final String dbPath;

    private Searcher searcher;

    public Ip2RegionSearcher(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * 查询一个ip地址的地理位置信息
     *
     * @param ip
     * @return
     */
    public IpRegion search(String ip) {
        try {
            String region = this.searcher.search(ip);
            if (StrUtil.isBlank(region)) {
                return null;
            }
            return this.formatter(region);
        } catch (Exception e) {
            throw new BizException(ErrorCode.SEARCH_IP_REGION_FAILED, e);
        }
    }

    /**
     * Ip2Region 插件的查询字符串结果转对象
     *
     * @param region
     * @return
     */
    private IpRegion formatter(String region) {
        String[] split = region.split(SPLIT_REGEX);
        Assert.isTrue(split.length >= 5, "无法解析Ip2Region结果" + region);
        IpRegion model = new IpRegion();
        model.setCountry(NOT_VALUE.equals(split[0]) ? null : split[0]);
        model.setRegion(NOT_VALUE.equals(split[1]) ? null : split[1]);
        model.setProvince(NOT_VALUE.equals(split[2]) ? null : split[2]);
        model.setCity(NOT_VALUE.equals(split[3]) ? null : split[3]);
        model.setIsp(NOT_VALUE.equals(split[4]) ? null : split[4]);
        return model;
    }

    /**
     * 初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 从 dbPath 加载整个 xdb 到内存，并创建一个完全基于内存的查询对象
        byte[] bytes = ResourceUtil.readBytes(this.dbPath);
        this.searcher = Searcher.newWithBuffer(bytes);
        log.info("Ip2Region Searcher init");
    }

    /**
     * 销毁
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        if (this.searcher != null) {
            this.searcher.close();
            this.searcher = null;
            log.info("Ip2Region Searcher destroy");
        }
    }
}
