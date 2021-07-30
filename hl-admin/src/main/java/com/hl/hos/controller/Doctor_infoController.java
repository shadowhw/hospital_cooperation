package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DoctorHos;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Hos_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-02
 */
@RestController
@RequestMapping("/hos/doctor_info")
public class Doctor_infoController {
    @Autowired
    private Doctor_infoService doctor_infoService;
    @Autowired
    private Hos_infoService hos_infoService;
    @Autowired
    private Result result;

    /**
     * 根据页码和状态查询所有医生信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_doctor_list")
    public Result get_doctor_list(String page,String limit,String stat)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<Doctor_info>()
                .eq("stat",stat);//排除协助医师
        List<Doctor_info> list = doctor_infoService.list(queryWrapper);
        List<DoctorHos> doctorLists = new ArrayList<DoctorHos>();

        Page<Doctor_info> page1 = new Page<Doctor_info>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Doctor_info> iPage = doctor_infoService.page(page1,queryWrapper);

        for (int i = 0; i < iPage.getRecords().size(); i++)
        {
            DoctorHos doctorHos = new DoctorHos();
            Doctor_info doctorInfo = iPage.getRecords().get(i);
            doctorInfo.setDoctor_pwd(null);
            doctorHos.setDoctor_info(doctorInfo);
            doctorHos.setHos_info(hos_infoService.getById(doctorInfo.getHos_id()));
            doctorLists.add(doctorHos);
        }
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(doctorLists);
        result.setCode(200);
        return result;
    }

    /**
     * 更新医生信息
     * @return
     */
    @ResponseBody
    @GetMapping("/update_doctor_info")
    public Result update_doctor_info(Doctor_info doctor_info)
    {
        if(doctor_infoService.updateById(doctor_info))
        {
            result.setCount(1);
            result.setData(null);
            result.setCode(200);
            result.setMsg("修改成功!");
        }else {
            result.setCount(1);
            result.setData(null);
            result.setCode(201);
            result.setMsg("修改失败!");
        }
        return result;
    }

    /**
     * 根据医院查询所属医院下的所有合法医生
     * @return
     */
    @ResponseBody
    @GetMapping("/get_doc_of_hos")
    public Result get_doc_of_hos(Long hos_id)
    {

        List<Doctor_info> doc_list = doctor_infoService.list(new QueryWrapper<Doctor_info>()
                .select("id","doctor_name","doctor_account")
                .eq("hos_id",hos_id)
                .eq("pass",1)//通过的医生
                .ne("stat",3)//状态正常的医生
                .ne("stat",4)
        );
        result.setCount(doc_list.size());//数量应该是所有数据的大小
        result.setData(doc_list);
        result.setCode(200);
        return result;
    }

    /**
     * 组合查询所有医生信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_doctor_list_by_params")
    public Result get_doctor_list_by_params(String doctor_name,String email,String doctor_tel,String doctor_account,String pass)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<>();
        //判断条件值是否为空,如果不为空,拼接条件
        if (!StringUtils.isEmpty(doctor_name.trim())){
            queryWrapper.like("doctor_name",doctor_name);
        }
        if (!StringUtils.isEmpty(email.trim())){
            queryWrapper.like("email",email);
        }
        if (!StringUtils.isEmpty(doctor_tel.trim())){
            queryWrapper.like("doctor_tel",doctor_tel);
        }
        if (!StringUtils.isEmpty(doctor_account.trim())){
            queryWrapper.like("doctor_account",doctor_account);
        }
        if (!StringUtils.isEmpty(pass.trim())){
            queryWrapper.like("pass",pass);
        }

        List<Doctor_info> doc_list = doctor_infoService.list(queryWrapper);
        List<DoctorHos> doctorLists = new ArrayList<DoctorHos>();

        for (int i = 0; i < doc_list.size(); i++)
        {
            DoctorHos doctorHos = new DoctorHos();
            Doctor_info doctorInfo = doc_list.get(i);
            doctorInfo.setDoctor_pwd(null);
            doctorHos.setDoctor_info(doctorInfo);
            doctorHos.setHos_info(hos_infoService.getById(doctorInfo.getHos_id()));
            doctorLists.add(doctorHos);
        }
        result.setCount(doc_list.size());//数量应该是所有数据的大小
        result.setData(doctorLists);
        result.setCode(200);
        return result;
    }
}

