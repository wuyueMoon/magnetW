package com.moon.controller;

import com.google.gson.Gson;
import com.moon.config.ApplicationConfig;
import com.moon.response.MagnetPageConfig;
import com.moon.response.MagnetPageData;
import com.moon.response.MagnetPageOption;
import com.moon.response.MagnetRule;
import com.moon.service.MagnetRuleService;
import com.moon.service.MagnetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * created 2019/5/5 17:53
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    ApplicationConfig config;

    @Autowired
    MagnetRuleService ruleService;

    @Autowired
    MagnetService magnetService;

    @RequestMapping(value = {"", "search"}, method = RequestMethod.GET)
    public String search(HttpServletRequest request, Model model, @RequestParam(required = false) String source, @RequestParam(value = "k", required = false) String keyword,
                         @RequestParam(value = "s", required = false) String sort, @RequestParam(value = "p", required = false) Integer page) throws Exception {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        boolean isMobile = !StringUtils.isEmpty(userAgent) && userAgent.toLowerCase().contains("mobile");

        //默认参数
        MagnetPageOption pageOption = magnetService.transformCurrentOption(source, keyword, sort, page);
        if (isMobile) {
            //手机的初始化不能跨页
            pageOption.setPage(1);
        }
        MagnetPageData data = new MagnetPageData();
        data.setCurrent(pageOption);

        MagnetRule rule = ruleService.getRuleBySite(source);

        Gson gson = new Gson();
        model.addAttribute("is_mobile", isMobile);
        model.addAttribute("current", gson.toJson(pageOption));
        model.addAttribute("config", gson.toJson(new MagnetPageConfig(config)));
        model.addAttribute("sort_by", gson.toJson(ruleService.getSupportedSorts(rule.getPaths())));
        model.addAttribute("source_sites", gson.toJson(ruleService.getSites()));

        return isMobile ? "mobile" : "index";
        //return "index";
    }


}
