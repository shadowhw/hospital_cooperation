package com.hl.hos.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hl.hos.mapper.Doctor_infoMapper;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.service.Doctor_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @Autowired
    private Doctor_infoService doctorService;


    @PostMapping("/userLogin")
    public String userLogin(@RequestParam("doctorName")String doctorName ,
                            @RequestParam("doctorPwd")String doctorPwd){

        Doctor_info doctorInfo = new Doctor_info();
        doctorInfo.setDoctor_name(doctorName);
        doctorInfo.setDoctor_pwd(doctorPwd);
        Doctor_info loginUser = doctorService.getOne(new QueryWrapper<Doctor_info>().gt("doctor_name",doctorName).gt("doctor_pwd",doctorInfo.getDoctor_pwd()));

        String addr = "";

        if(loginUser!=null){
            Integer stat = loginUser.getStat();//获取状态码
            if(stat == 0){ //管理员

            }else if(stat == 1){ //上传医师

            }else{ //协作医师

            }
        }



        return "";
    }
}
