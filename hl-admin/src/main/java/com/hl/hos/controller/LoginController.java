package com.hl.hos.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hl.hos.mapper.Doctor_infoMapper;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Hos_infoService;
import com.hl.hos.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private Doctor_infoService doctorService;

    @Autowired
    private Hos_infoService hosService;


    @PostMapping("/userLogin")
    public String userLogin(@RequestParam("doctorName")String hos_name ,
                            @RequestParam("doctorPwd")String doctorPwd ,
                            HttpSession session ){

//        //查询有误单位
//        Hos_info hosInfo = hosService.getOne(new QueryWrapper<Hos_info>().eq("hos_name",hos_name));
//        if(hosInfo == null){
//            session.setAttribute("error","请检查您的单位名称");
//            return "redirect:/login";
//        }
//        //根据hosId查询医生
//        Doctor_info doctorInfo = doctorService.getOne(new QueryWrapper<Doctor_info>().eq("hos_id", hosInfo.getId()));
//        if(doctorInfo == null){
//            session.setAttribute("error","账号或者密码错误");
//            return "redirect:/login";
//        }
        //验证密码
        String md5Pwd = MD5Util.getMd5(doctorPwd);//验证
        Doctor_info doctorRes = doctorService.getOne(new QueryWrapper<Doctor_info>().eq("doctor_account", hos_name).eq("doctor_pwd", md5Pwd));
        if(doctorRes == null){
            session.setAttribute("error","账号或者密码错误");
            return "redirect:/login";
        }


        Integer pass = doctorRes.getPass();
        if(pass==0){ //账号没激活
            session.setAttribute("error","账号没有激活");
            return "redirect:/login";
        }
        if(doctorRes.getStat()==3){ //停用
            session.setAttribute("error","账号停用");
            return "redirect:/login";
        }
        String addr = "/login";
        Integer stat = doctorRes.getStat();//获取状态码
        session.setAttribute("doctor_info",doctorRes);
        if(stat == 0){ //管理员
            addr = "admin";
        }else if(stat == 1){ //协作医师
            addr = "assist_index";
        }else{ //上传者医师
            addr = "assist_index";
        }


        if(addr.equals("/login")) session.setAttribute("error","账号或者密码错误");

        return "redirect:/"+addr;
    }

    @GetMapping("/assist")
    public String getess(){
        return "assistIndex";
    }

    @GetMapping("/logOut")
    @ResponseBody
    public String logOut(HttpSession session){
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        session.removeAttribute("doctor_info");
        return "see u "+doctor_info.getDoctor_name();
    }
}
