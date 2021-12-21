package com.hl.hos.handler;

import com.hl.hos.pojo.Doctor_info;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Doctor_info doctor_info =(Doctor_info)request.getSession().getAttribute("doctor_info");
        if(doctor_info==null)
        {

            return true;
        }else if(doctor_info.getStat() == 0){
            response.sendRedirect("/admin");
            return false;
        }else if(doctor_info.getStat() == 1 || doctor_info.getStat()==2){
            response.sendRedirect("/assist_index");
            return false;
        }
        return true;
    }
}
