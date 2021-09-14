package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DiagnosisDoctorHos;
import com.hl.hos.backPogo.DoctorHos;
import com.hl.hos.pojo.*;
import com.hl.hos.service.Disgnose_infoService;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Doctor_with_disgnoseService;
import com.hl.hos.service.Hos_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 诊断信息页面
 * @author 何夜息
 * @since 2021-07-08
 */
@RestController
@RequestMapping("/hos/diagnosis_info")
public class Diagnosis_infoController
{
    @Autowired
    private Doctor_infoService doctor_infoService;
    @Autowired
    private Disgnose_infoService disgnose_infoService;
    @Autowired
    private Hos_infoService hos_infoService;
    @Autowired
    private Doctor_with_disgnoseService doctor_with_disgnoseService;
    @Autowired
    private Result result;

    /**
     * 查询诊断信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_diagnosis_list")
    public Result get_diagnosis_list(String page,String limit)
    {
        QueryWrapper<Disgnose_info> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        queryWrapper.ne("stat",0);
        //不包括状态0：保存
        List<Disgnose_info> list = disgnose_infoService.list(queryWrapper);
        List<DiagnosisDoctorHos> resList = new ArrayList<DiagnosisDoctorHos>();

        Page<Disgnose_info> page1 = new Page<Disgnose_info>(Integer.parseInt(page),Integer.parseInt(limit));

        IPage<Disgnose_info> iPage = disgnose_infoService.page(page1,queryWrapper);

        for (int i = 0; i < iPage.getRecords().size(); i++)
        {
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            Disgnose_info disgnose_info = iPage.getRecords().get(i);

            //医生账号不能用时不显示
            Doctor_info doctorInfo = doctor_infoService.getById(disgnose_info.getDoctor_id());

            //查询协作医师姓名
            List<Doctor_with_disgnose> doctor_with_disgnose = doctor_with_disgnoseService.list(new QueryWrapper<Doctor_with_disgnose>()
                .eq("disgnose_id",disgnose_info.getId())
            );
            if(doctor_with_disgnose.size()>=1)
            {
                Doctor_info assist = doctor_infoService.getById(doctor_with_disgnose.get(0).getDoctor_id());
                diagnosisDoctorHos.setAssist_doctor_name(assist.getDoctor_name());
            }else {
                diagnosisDoctorHos.setAssist_doctor_name("暂无");
            }

            //医生账号被删除
            if(doctorInfo==null)
                continue;
            if(doctorInfo.getStat()==5 || doctorInfo.getStat()==6)
                continue;//不绑定信息
            diagnosisDoctorHos.setDisgnose_info(disgnose_info);//绑定诊断信息
            doctorInfo.setDoctor_pwd(null);
            diagnosisDoctorHos.setDoctor_info(doctorInfo);//绑定医生
            Long hos_id = doctor_infoService.getById(disgnose_info.getDoctor_id()).getHos_id();
            diagnosisDoctorHos.setHos_info(hos_infoService.getById(hos_id));//绑定医院信息

            resList.add(diagnosisDoctorHos);
        }
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(resList);
        result.setCode(200);
        return result;
    }

    /**
     * 根据诊断编号查询数据
     * @return
     */
    @ResponseBody
    @GetMapping("/get_diagnosis_by_id")
    public Result get_diagnosis_by_id(Long id)
    {
        List<Disgnose_info> list = disgnose_infoService.list(new QueryWrapper<Disgnose_info>()
                .eq("id",id)
        );
        List<DiagnosisDoctorHos> resList = new ArrayList<DiagnosisDoctorHos>();

        for (int i = 0; i < list.size(); i++)
        {
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            Disgnose_info disgnose_info = list.get(i);

            diagnosisDoctorHos.setDisgnose_info(disgnose_info);//绑定诊断信息
            Doctor_info doctorInfo = doctor_infoService.getById(disgnose_info.getDoctor_id());
            //查询协作医师姓名
            List<Doctor_with_disgnose> doctor_with_disgnose = doctor_with_disgnoseService.list(new QueryWrapper<Doctor_with_disgnose>()
                    .eq("disgnose_id",disgnose_info.getId())
            );
            if(doctor_with_disgnose.size()>=1)
            {
                Doctor_info assist = doctor_infoService.getById(doctor_with_disgnose.get(0).getDoctor_id());
                diagnosisDoctorHos.setAssist_doctor_name(assist.getDoctor_name());
            }else {
                diagnosisDoctorHos.setAssist_doctor_name("暂无");
            }

            diagnosisDoctorHos.setDoctor_info(doctorInfo);//绑定医生
            Long hos_id = doctor_infoService.getById(disgnose_info.getDoctor_id()).getHos_id();
            diagnosisDoctorHos.setHos_info(hos_infoService.getById(hos_id));//绑定医院信息
            doctorInfo.setDoctor_pwd(null);
            resList.add(diagnosisDoctorHos);
        }
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(resList);
        result.setCode(200);
        return result;
    }

    /**
     * 更新诊断信息
     * @return
     */
    @ResponseBody
    @PostMapping("/update_diagnosis_info")
    public Result update_diagnosis_info(Disgnose_info disgnose_info)
    {
        if(disgnose_infoService.updateById(disgnose_info))
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
     * 组合查询诊断信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_diagnosis_list_by_params")
    public Result get_diagnosis_list_by_params(String disgnose_code,String patient_name,String patient_tall,String patient_weight,String diagnose_result,String create_time,String stat)
    {
        QueryWrapper<Disgnose_info> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(disgnose_code.trim())){
            queryWrapper.like("disgnose_code",disgnose_code);
        }
        if (!StringUtils.isEmpty(patient_name.trim())){
            queryWrapper.like("patient_name",patient_name);
        }
        if (!StringUtils.isEmpty(patient_tall.trim())){
            queryWrapper.like("patient_tall",patient_tall);
        }
        if (!StringUtils.isEmpty(patient_weight.trim())){
            queryWrapper.like("patient_weight",patient_weight);
        }
        if (!StringUtils.isEmpty(diagnose_result.trim())){
            queryWrapper.like("diagnose_result",diagnose_result);
        }
        if (!StringUtils.isEmpty(create_time.trim())){
            queryWrapper.like("create_time",create_time);
        }
        if (!StringUtils.isEmpty(stat.trim())){
            queryWrapper.like("stat",stat);
        }
        queryWrapper.orderByDesc("create_time");
        List<Disgnose_info> list = disgnose_infoService.list(queryWrapper);

        List<DiagnosisDoctorHos> resList = new ArrayList<DiagnosisDoctorHos>();


        for (int i = 0; i < list.size(); i++)
        {
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            Disgnose_info disgnose_info = list.get(i);

            //查询协作医师姓名
            List<Doctor_with_disgnose> doctor_with_disgnose = doctor_with_disgnoseService.list(new QueryWrapper<Doctor_with_disgnose>()
                    .eq("disgnose_id",disgnose_info.getId())
            );
            if(doctor_with_disgnose.size()>=1)
            {
                Doctor_info assist = doctor_infoService.getById(doctor_with_disgnose.get(0).getDoctor_id());
                diagnosisDoctorHos.setAssist_doctor_name(assist.getDoctor_name());
            }else {
                diagnosisDoctorHos.setAssist_doctor_name("暂无");
            }

            Doctor_info doctorInfo = doctor_infoService.getById(disgnose_info.getDoctor_id());
            if(doctorInfo.getStat()==5 || doctorInfo.getStat()==6)
                continue;//不绑定信息
            doctorInfo.setDoctor_pwd(null);
            diagnosisDoctorHos.setDoctor_info(doctorInfo);//绑定医生
            Long hos_id = doctor_infoService.getById(disgnose_info.getDoctor_id()).getHos_id();
            diagnosisDoctorHos.setHos_info(hos_infoService.getById(hos_id));//绑定医院信息
            diagnosisDoctorHos.setDisgnose_info(disgnose_info);//绑定诊断信息
            resList.add(diagnosisDoctorHos);
        }
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(resList);
        result.setCode(200);
        return result;
    }

    /**
     * 图表1的组合查询诊断信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_diagnosis_list_by_params_from_chart1")
    public Result get_diagnosis_list_by_params_from_chart1(String disgnose_code,String patient_name,String patient_tall,String patient_weight,String diagnose_result,String create_time,String start_time,String end_time)
    {
        QueryWrapper<Disgnose_info> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(disgnose_code.trim())){
            queryWrapper.like("disgnose_code",disgnose_code);
        }
        if (!StringUtils.isEmpty(patient_name.trim())){
            queryWrapper.like("patient_name",patient_name);
        }
        if (!StringUtils.isEmpty(patient_tall.trim())){
            queryWrapper.like("patient_tall",patient_tall);
        }
        if (!StringUtils.isEmpty(patient_weight.trim())){
            queryWrapper.like("patient_weight",patient_weight);
        }
        if (!StringUtils.isEmpty(diagnose_result.trim())){
            queryWrapper.like("diagnose_result",diagnose_result);
        }
        if (!StringUtils.isEmpty(create_time.trim())){
            queryWrapper.like("create_time",create_time);
        }
        if (!StringUtils.isEmpty(start_time.trim())){
            queryWrapper.ge("create_time",start_time);//大于等于开始时间
            queryWrapper.le("create_time",end_time);//小于等于结束时间
        }


        List<Disgnose_info> list = disgnose_infoService.list(queryWrapper);

        List<DiagnosisDoctorHos> resList = new ArrayList<DiagnosisDoctorHos>();


        for (int i = 0; i < list.size(); i++)
        {
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            Disgnose_info disgnose_info = list.get(i);

            diagnosisDoctorHos.setDisgnose_info(disgnose_info);//绑定诊断信息
            Doctor_info doctorInfo = doctor_infoService.getById(disgnose_info.getDoctor_id());
            doctorInfo.setDoctor_pwd(null);
            diagnosisDoctorHos.setDoctor_info(doctorInfo);//绑定医生
            Long hos_id = doctor_infoService.getById(disgnose_info.getDoctor_id()).getHos_id();
            diagnosisDoctorHos.setHos_info(hos_infoService.getById(hos_id));//绑定医院信息

            resList.add(diagnosisDoctorHos);
        }
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(resList);
        result.setCode(200);
        return result;
    }


}

