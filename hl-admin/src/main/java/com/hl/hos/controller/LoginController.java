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
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private Doctor_infoService doctorService;


    @PostMapping("/userLogin")
    public String userLogin(@RequestParam("doctorName")String hos_name ,
                            @RequestParam("doctorPwd")String doctorPwd ,
                            HttpSession session ){

        Doctor_info doctorInfo = new Doctor_info();
        doctorInfo.setDoctor_pwd(doctorPwd);
        //
        Doctor_info loginUser = doctorService.getOne(new QueryWrapper<Doctor_info>().gt("doctor_pwd",doctorInfo.getDoctor_pwd()));

        if(loginUser == null){
            session.setAttribute("error","账号或者密码错误");
            return "redirect:/login";
        }

        Integer pass = loginUser.getPass();
        if(pass==0){ //账号没激活
            session.setAttribute("error","账号没有激活");
            return "redirect:/login";
        }

        String addr = "/login";
        Integer stat = loginUser.getStat();//获取状态码
        session.setAttribute("doctor_info",doctorInfo);
        if(stat == 0){ //管理员
            addr = "adminIndex";
        }else if(stat == 1){ //上传医师
            addr = "assistIndex";
        }else{ //协作医师
            addr = "otherIndex";
        }


        if(addr.equals("/login")) session.setAttribute("error","账号或者密码错误");

        return "redirect:/"+addr;
    }
}
