package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * page目录下用户
 */
@Controller
@RequestMapping("/pages")
public class PageCommonDataController
{
    @GetMapping("/commonData/processData")
    public String processData()
    {
        return "pages/commonData/processData";
    }
    @GetMapping("/commonData/submitData")
    public String submitData()
    {
        return "pages/commonData/submitData";
    }
    @GetMapping("/commonData/uploadData")
    public String uploadData()
    {
        return "pages/commonData/uploadData";
    }
}
