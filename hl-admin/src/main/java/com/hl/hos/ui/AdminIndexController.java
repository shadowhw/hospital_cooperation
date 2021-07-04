package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminIndexController
{
    /**
     * 管理员首页
     * @return
     */
    @GetMapping("/admin")
    public String adminIndex()
    {
        return "adminIndex";
    }

    /**
     * 协作医师首页
     * @return
     */
    @GetMapping("/assist_index")
    public String assist_index()
    {
        return "assistIndex";
    }

    /**
     * 申请人首页
     * @return
     */
    @GetMapping("/otherIndex")
    public String other_index()
    {
        return "otherIndex";
    }

    /**
     * 登录页面
     * @return
     */
    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

}
