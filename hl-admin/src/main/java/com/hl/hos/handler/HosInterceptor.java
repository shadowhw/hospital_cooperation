package com.hl.hos.handler;

import com.hl.hos.pojo.Doctor_info;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HosInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //获取用户session
        Doctor_info doctor_info =(Doctor_info)request.getSession().getAttribute("doctor_info");
        if(doctor_info==null)
        {
            request.getRequestDispatcher("/login").forward(request,response);
            return false;
        }
        return true;
    }
}
