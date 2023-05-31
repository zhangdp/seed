package com.zhangdp.seed.controller.sys;

import com.zhangdp.seed.common.component.Ip2RegionSearcher;
import com.zhangdp.seed.model.IpRegion;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2023/5/30
 *
 * @author zhangdp
 * @since
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    private final Ip2RegionSearcher searcher;

    @GetMapping("/ip/region/{ip}")
    public IpRegion region(@PathVariable String ip) {
        return searcher.search(ip);
    }

}
