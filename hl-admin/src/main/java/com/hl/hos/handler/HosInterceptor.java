package com.hl.hos.handler;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HosInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //获取用户session
        Object doctor_info = request.getSession().getAttribute("doctor_info");
        if(doctor_info==null)
        {
            request.getRequestDispatcher("/login").forward(request,response);
            return false;
        }
        return true;
    }
}
