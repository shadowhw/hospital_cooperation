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
import com.hl.hos.utils.DateUtil;
import com.hl.hos.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import javax.servlet.http.HttpSession;
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
    public Result get_doctor_list(String page,String limit,int type)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<>();//排除协助医师
        //协助医师
        if(type==1)
        {
            queryWrapper.in("stat",1,3,4);
        }
        //普通医师
        if(type==2)
        {
            queryWrapper.in("stat",2,5,6);
        }

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
        if(doctor_info.getDoctor_pwd()!=null)
            doctor_info.setDoctor_pwd(MD5Util.getMd5(doctor_info.getDoctor_pwd()));
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
                .in("stat",0,1,2,3)//状态正常的医生
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
    public Result get_doctor_list_by_params(String doctor_name,String email,String doctor_tel,String doctor_account,String pass,int type)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<>();
        //协助医师
        if(type==1)
        {
            queryWrapper.in("stat",1,3,4);
        }
        //普通医师
        if(type==2)
        {
            queryWrapper.in("stat",2,5,6);
        }
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

    /**
     * 添加协作医师
     * @param doctor_info
     * @param hos_info
     * @return
     */
    @PostMapping("/add_assist_doctor")
    @ResponseBody
    public Result add_assist_doctor(Doctor_info doctor_info, Hos_info hos_info)
    {
        Long hos_id = 0l;
        //查询单位信息
        List<Hos_info> list = hos_infoService.list(new QueryWrapper<Hos_info>()
                .eq("hos_name",hos_info.getHos_name())
                .eq("hos_addr",hos_info.getHos_addr())
        );
//        if(list.size()>=1)
//        {
//            result.setCode(201);
//            result.setData(null);
//            result.setMsg("该单位账号已存在");
//            return result;
//        }
        //不存在就添加单位
        hos_info.setStat(1);
        hos_info.setCreate_time(DateUtil.getNowSqlDateTime());
        if(list.size()==0)
        {
            hos_infoService.save(hos_info);
            hos_id = hos_info.getId();
        }else {
            hos_id = list.get(0).getId();
        }

        //添加医生
        doctor_info.setCreate_time(DateUtil.getNowSqlDateTime());
        doctor_info.setPass(1);
        doctor_info.setHos_id(hos_id);
        doctor_info.setStat(1);//协作医师
        doctor_info.setDoctor_pwd(MD5Util.getMd5(doctor_info.getDoctor_tel()));//默认密码就是电话号码
        //设置医生账号：医院名加该医院数量
        List<Doctor_info> hos_doctors = doctor_infoService.list(new QueryWrapper<Doctor_info>()
                .eq("hos_id",hos_id)
        );
        doctor_info.setDoctor_account(hos_info.getHos_name()+(hos_doctors.size()+1));
        doctor_infoService.save(doctor_info);

        result.setMsg("成功");
        result.setCode(200);
        return result;
    }

    @PostMapping("/updatePwdByP")
    public Result result(String doctorId, String pwd, HttpSession session){
        Result result = new Result();

        Doctor_info doctorInfoById = doctor_infoService.getById(doctorId);
        if(doctorInfoById == null){
            result.setCode(500);
        }else {
            String newPwd1 = MD5Util.getMd5(pwd);

            doctorInfoById.setDoctor_pwd(newPwd1); //更改密码
            boolean b = doctor_infoService.updateById(doctorInfoById);
            if(b){
                result.setCode(0);
                session.removeAttribute("doctor_info");
            }else{
                result.setCode(500);
            }
        }

        return result;

    }
}

