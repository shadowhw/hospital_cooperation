package com.hl.hos.handler;

import com.hl.hos.pojo.Doctor_info;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//管理员拦截器：防止用户进入管理页面
public class AdminInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //获取用户session
        Doctor_info doctor_info = (Doctor_info)request.getSession().getAttribute("doctor_info");

        if(doctor_info.getStat()!=0)
        {
            request.getRequestDispatcher("/login").forward(request,response);
            return false;
        }
        return true;
    }
}
