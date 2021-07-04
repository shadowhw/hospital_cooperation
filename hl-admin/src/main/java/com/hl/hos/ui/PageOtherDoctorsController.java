package com.hl.hos.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * page目录下协助医师
 */
@Controller
@RequestMapping("/pages")
public class PageOtherDoctorsController
{
    @GetMapping("/other_doctors/chart/chart")
    public String other_doctors_chart()
    {
        return "pages/otherDoctors/chart/chart";
    }
    @GetMapping("/other_doctors/apply")
    public String other_doctors_apply()
    {
        return "pages/otherDoctors/apply";
    }
    @GetMapping("/other_doctors/askDiagnosisList")
    public String other_doctors_askDiagnosisList()
    {
        return "pages/otherDoctors/askDiagnosisList";
    }
    @GetMapping("/other_doctors/diagnosisImport")
    public String other_doctors_diagnosisImport()
    {
        return "pages/otherDoctors/diagnosisImport";
    }
    @GetMapping("/other_doctors/selectForm")
    public String other_doctors_selectForm()
    {
        return "pages/otherDoctors/selectForm";
    }

}
