package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminIndexController
{
    @GetMapping("/admin")
    public String adminIndex()
    {
        return "login";
    }
}
