package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Doctor_with_disgnose;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Doctor_with_disgnoseService;
import com.hl.hos.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
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
@RequestMapping("/hos/doctor_with_disgnose")
public class Doctor_with_disgnoseController {
    @Autowired
    private Doctor_with_disgnoseService doctor_with_disgnoseService;
    @Autowired
    private Result result;
    @Autowired
    private Doctor_infoService doctor_infoService;

    /**
     * 添加协助医师协助信息
     * @return
     */
    @ResponseBody
    @PostMapping("/add_assist")
    public Result add_assist(Doctor_with_disgnose doctor_with_disgnose,String doctor_account)
    {
        //分配医生有记录不做修改
        List<Doctor_with_disgnose> list = doctor_with_disgnoseService.list(new QueryWrapper<Doctor_with_disgnose>()
            .eq("doctor_id",doctor_with_disgnose.getDoctor_id())
            .eq("disgnose_id",doctor_with_disgnose.getDisgnose_id())
        );
        if(list.size()>=1)
        {
            result.setCount(1);
            result.setData(null);
            result.setCode(200);
            result.setMsg("成功!");
            return result;
        }
        doctor_with_disgnose.setCreate_time(DateUtil.getNowSqlDateTime());
        doctor_with_disgnose.setStat(1);

        //根据医师编号
        Doctor_info doctorInfo = doctor_infoService.getOne(new QueryWrapper<Doctor_info>()
            .eq("doctor_account",doctor_account)
        );
        doctor_with_disgnose.setDoctor_id(doctorInfo.getId());
        if(doctor_with_disgnoseService.save(doctor_with_disgnose))
        {
            result.setCount(1);
            result.setData(null);
            result.setCode(200);
            result.setMsg("添加成功!");
        }else {
            result.setCount(0);
            result.setData(null);
            result.setCode(201);
            result.setMsg("修改失败!");
        }
        return result;
    }
}

