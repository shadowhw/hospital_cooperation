package com.hl.hos.controller;


import com.hl.hos.mapper.Disgnose_infoMapper;
import com.hl.hos.pojo.Disgnose_info;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.service.Disgnose_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-02
 */
@RestController
//@RequestMapping("/hos/disgnose_info")
public class Disgnose_infoController {

    @Autowired
    private Disgnose_infoService disgnoseInfoService;
    /**
     * 保存症断书And附件
     * @return
     */
    @PostMapping("/saveDiagnose_with_attached")
    public String saveDiagnose_with_attached(@RequestParam("file")MultipartFile[] multipartFiles,
                                             @RequestParam("patient_hos")String patientHos,
                                             @RequestParam("patient_name")String patientName,
                                             @RequestParam("patient_birth")String patientBirth,
                                             @RequestParam("patient_tall")String patientTall,
                                             @RequestParam("patient_weight")String patientWeight,
                                             @RequestParam("diagnose_result")String patientResult,
                                             HttpServletRequest request,
                                             HttpSession session){
        //取出医师信息
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        //存放诊断表信息
        Disgnose_info disgnose_info = new Disgnose_info();
        disgnose_info.setDoctor_id(doctor_info.getId());
        disgnose_info.setPatient_name(patientName);
        disgnose_info.setPatient_birth(Timestamp.valueOf(patientBirth));
        disgnose_info.setPatient_tall(patientTall);
        disgnose_info.setPatient_weight(patientWeight);
        disgnose_info.setDiagnose_result(patientResult);


        String path = request.getServletContext().getRealPath("/upload");

        return "" ;
    }


}

