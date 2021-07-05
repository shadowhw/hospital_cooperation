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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
        //查询单位信息
        List<Hos_info> list = hos_infoService.list(new QueryWrapper<Hos_info>()
            .eq("hos_name",hos_info.getHos_name())
                .eq("hos_addr",hos_info.getHos_addr())
        );
        if(list.size()>=1)
        {
            result.setCode(201);
            result.setData(null);
            result.setMsg("该单位账号已存在");
            return result;
        }
        //添加单位
        hos_info.setStat(1);
        hos_info.setCreate_time(DateUtil.getNowSqlDateTime());
        hos_infoService.save(hos_info);

        //添加医生
        doctor_info.setCreate_time(DateUtil.getNowSqlDateTime());
        doctor_info.setPass(0);
        doctor_info.setHos_id(hos_info.getId());
        doctor_info.setStat(2);//上传者
        doctor_info.setDoctor_pwd(MD5Util.getMd5("123456"));
        doctor_infoService.save(doctor_info);

        result.setMsg("成功");
        result.setCode(200);
        return result;
    }
}
