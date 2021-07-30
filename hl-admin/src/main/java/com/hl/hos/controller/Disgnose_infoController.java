package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DiagnosisDoctorHos;
import com.hl.hos.mapper.Disgnose_infoMapper;
import com.hl.hos.pojo.Disgnose_info;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Disgnose_infoService;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Hos_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    Disgnose_infoService disgnoseInfoService; //诊断申请
    @Autowired
    Doctor_infoService doctorInfoService; //医生
    @Autowired
    Hos_infoService hosInfoService; //医院
    @GetMapping("/getDisgnoseWithDoctorInfo")
    public Result getDisgnoseWithDoctorInfo(HttpSession session
                                                ,@RequestParam(defaultValue ="1") Integer page){
        Doctor_info doctor_info = (Doctor_info) session.getAttribute("doctor_info");

        QueryWrapper<Disgnose_info> q1 = new QueryWrapper<Disgnose_info>().eq("doctor_id", doctor_info.getId());
        //根据医生查出医院以及他的诊断申请
        Page<Disgnose_info> disgnose_infoPage = disgnoseInfoService.page(new Page<Disgnose_info>(page, 10), q1);
        Page<Hos_info> hos_infoPage = hosInfoService.page(new Page<Hos_info>(page,10), new QueryWrapper<Hos_info>().eq("id",doctor_info.getHos_id()));

        List<DiagnosisDoctorHos> diagnosisDoctorHosList = new ArrayList<>();

        List<Disgnose_info> disgnose_infoPageRecords = disgnose_infoPage.getRecords();
        List<Hos_info> hos_infoPageRecords = hos_infoPage.getRecords();
        //添加诊断申请表
        for(int i =0;i<disgnose_infoPageRecords.size();i++){
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            diagnosisDoctorHos.setDoctor_info(doctor_info);
            diagnosisDoctorHos.setHos_info(hos_infoPageRecords.get(0)); //添加医院
            diagnosisDoctorHos.setDisgnose_info(disgnose_infoPageRecords.get(i));
            diagnosisDoctorHosList.add(diagnosisDoctorHos);
        }
        Result result = new Result();
        result.setCode(0);
        result.setCount(diagnosisDoctorHosList.size());
        result.setData(diagnosisDoctorHosList);
        return result;
    }
}

