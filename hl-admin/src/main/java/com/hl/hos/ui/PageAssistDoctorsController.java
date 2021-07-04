package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * page目录下协助医师
 */
@Controller
@RequestMapping("/pages")
public class PageAssistDoctorsController
{
    @GetMapping("/assist_doctors/apply")
    public String apply()
    {
        return "pages/assistDoctors/apply";
    }
    @GetMapping("/assist_doctors/askDiagnosisList")
    public String askDiagnosisList()
    {
        return "pages/assistDoctors/askDiagnosisList";
    }
    @GetMapping("/assist_doctors/selectForm")
    public String selectForm()
    {
        return "pages/assistDoctors/selectForm";
    }
}
