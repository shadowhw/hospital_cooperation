package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * page目录下用户
 */
@Controller
@RequestMapping("/pages")
public class PageUserController
{
    @GetMapping("/user/register")
    public String register()
    {
        return "pages/user/register";
    }
    @GetMapping("/user/user")
    public String user()
    {
        return "pages/user/user";
    }
    @GetMapping("/user/user_pwd")
    public String user_pwd()
    {
        return "pages/user/user_pwd";
    }
}
