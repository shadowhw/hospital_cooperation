package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DoctorHos;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Hos_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/hos/doctor_info")
public class Doctor_infoController {
    @Autowired
    private Doctor_infoService doctor_infoService;
    @Autowired
    private Hos_infoService hos_infoService;
    @Autowired
    private Result result;

    /**
     * 查询所有医生信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_doctor_list")
    public Result get_doctor_list(String page,String limit)
    {
        List<DoctorHos> doctorLists = new ArrayList<>();
        Page<Doctor_info> page1 = new Page<>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Doctor_info> iPage = doctor_infoService.page(page1);

        for (int i = 0; i < iPage.getRecords().size(); i++)
        {
            DoctorHos doctorHos = new DoctorHos();
            Doctor_info doctorInfo = iPage.getRecords().get(i);
            doctorInfo.setDoctor_pwd(null);
            doctorHos.setDoctor_info(doctorInfo);
            doctorHos.setHos_info(hos_infoService.getById(doctorInfo.getHos_id()));
            doctorLists.add(doctorHos);
        }
        result.setCount(iPage.getRecords().size());
        result.setData(doctorLists);
        result.setCode(200);
        return result;
    }


}

