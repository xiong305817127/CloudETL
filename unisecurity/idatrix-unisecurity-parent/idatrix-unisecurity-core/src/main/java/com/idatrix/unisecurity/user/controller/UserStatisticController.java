package com.idatrix.unisecurity.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 前台暂时是没有看到这样的url，不给予处理。
 */
@ApiIgnore
@RequestMapping("/user_statistic")
@RestController
public class UserStatisticController {

    /*@Autowired
    private UserStatisticService userStatisticService;

    @RequestMapping(value = "/recent_visit", method = RequestMethod.GET)
    public List<UserStatistic> recentVisit(Long userId) {
        List<UserStatistic> statistics = userStatisticService.selectByRecent(userId);
        return statistics;
    }

    @RequestMapping(value = "/user_sys_add_log", method = RequestMethod.POST)
    public Map<String, Object> userSysAddLog(UserStatistic userStatistic) {
        Map resultMap = ResultVoUtils.resultMap();
        try {
            userStatistic.setLastAccessed(new Date());
            userStatisticService.insertSelective(userStatistic);
        } catch (Exception e) {
            resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
            resultMap.put("message", "访问日志记录失败，请刷新后再试！");
            LoggerUtils.fmtError(getClass(), e, "记录用户访问系统日志错误。来源[%s]", userStatistic.toString());
        }
        resultMap.put("status",HttpCodeUtils.NORMAL_STATUS);
        resultMap.put("message","访问日志记录成功");
        return resultMap;
    }*/
}
