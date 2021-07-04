package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * page目录下的管理员页面
 */
@Controller
@RequestMapping("/pages")
public class PageAdminController
{
    @GetMapping("/admin/chart1")
    public String chart1()
    {
        return "pages/admin/chart/chart1";
    }
    @GetMapping("/admin/chart2")
    public String chart2()
    {
        return "pages/admin/chart/chart2";
    }
    @GetMapping("/admin/chart3")
    public String chart3()
    {
        return "pages/admin/chart/chart3";
    }

    @GetMapping("/admin/diagnosis")
    public String diagnosis()
    {
        return "pages/admin/diagnosis";
    }
    @GetMapping("/admin/homepage")
    public String homepage()
    {
        return "pages/admin/homepage";
    }
    @GetMapping("/admin/register")
    public String register()
    {
        return "pages/admin/register";
    }
    @GetMapping("/admin/waitingForDistribution")
    public String waitingForDistribution()
    {
        return "pages/admin/waitingForDistribution";
    }

}
