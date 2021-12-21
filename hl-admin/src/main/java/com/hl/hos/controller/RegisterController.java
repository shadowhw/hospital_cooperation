package com.hl.hos.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Hos_infoService;
import com.hl.hos.utils.DateUtil;
import com.hl.hos.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class RegisterController
{
    @Autowired
    private Result result;
    @Autowired
    private Hos_infoService hos_infoService;
    @Autowired
    private Doctor_infoService doctor_infoService;


    @PostMapping("/register_doctor")
    @ResponseBody
    public Result register_doctor(Doctor_info doctor_info, Hos_info hos_info)
    {
        Long hos_id = 0l;
        //查询单位信息
        List<Hos_info> list = hos_infoService.list(new QueryWrapper<Hos_info>()
            .eq("hos_name",hos_info.getHos_name())
                .eq("hos_addr",hos_info.getHos_addr())
        );
//        if(list.size()>=1)
//        {
//            result.setCode(201);
//            result.setData(null);
//            result.setMsg("该单位账号已存在");
//            return result;
//        }
        //不存在就添加单位,状态为2待审核
        hos_info.setStat(2);
        hos_info.setCreate_time(DateUtil.getNowSqlDateTime());
        if(list.size()==0)
        {
            hos_infoService.save(hos_info);
            hos_id = hos_info.getId();
        }else {
            hos_id = list.get(0).getId();
        }

        //添加医生
        doctor_info.setCreate_time(DateUtil.getNowSqlDateTime());
        doctor_info.setPass(0);
        doctor_info.setHos_id(hos_id);
        doctor_info.setStat(2);//上传者
        doctor_info.setDoctor_pwd(MD5Util.getMd5("123456"));//初始密码
        //设置医生账号：医院名加该医院数量
        List<Doctor_info> hos_doctors = doctor_infoService.list(new QueryWrapper<Doctor_info>()
            .eq("hos_id",hos_id)
        );
        String accountName = "";
        if(hos_doctors.size() <9){ //小于9添加0
            accountName = hos_info.getHos_name()+"D"+"0"+(hos_doctors.size()+1);
        }else{
            accountName = hos_info.getHos_name()+"D"+(hos_doctors.size()+1);
        }
        doctor_info.setDoctor_account(accountName);
        doctor_infoService.save(doctor_info);

        result.setMsg("成功");
        result.setCode(200);
        return result;
    }
}
