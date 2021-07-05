package com.hl.hos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @PostMapping("/userLogin")
    public String userLogin(@RequestParam("roleId") String role){
        System.out.println(role);
        String addr = "" ;
        if(role!= null)
            if(role.equals("0")){ //管理员
                addr = "admin";
            }else if(role.equals("1")){//上传者医师
                addr = "assist_index";
            }else { //协作医师
                addr = "otherIndex";
            }
        return "redirect:/"+addr;
    }
}
