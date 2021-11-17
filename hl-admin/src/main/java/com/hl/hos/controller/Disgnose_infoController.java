package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DiagnosisDoctorHos;
import com.hl.hos.mapper.Disgnose_infoMapper;
import com.hl.hos.pojo.*;
import com.hl.hos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 简一
 * @since 2021-07-02
 */
@RestController
//@RequestMapping("/hos/disgnose_info")
public class Disgnose_infoController {

    @Autowired
    Disgnose_infoService disgnoseInfoService; //诊断申请
    @Autowired
    Doctor_infoService doctorInfoService; //医生
    @Autowired
    Hos_infoService hosInfoService; //医院
    @Autowired
    Doctor_with_disgnoseService doctorWithDisgnoseService ; //分配表
    @Autowired
    AttachedService attachedService;


    /**
     * 分页获取当前协作医师的诊断请求
     * @param session
     * @param page
     * @return
     */
    @GetMapping("/getDisgnoseWithDoctorInfo")
    public Result getDisgnoseWithDoctorInfo(HttpSession session
                                                ,String page,String limit){
        Doctor_info doctor_info = (Doctor_info) session.getAttribute("doctor_info");
        List<Disgnose_info> list = disgnoseInfoService.list(new QueryWrapper<Disgnose_info>().eq("doctor_id",doctor_info.getId()));//所有分页

        QueryWrapper<Disgnose_info> q1 = new QueryWrapper<Disgnose_info>().eq("doctor_id", doctor_info.getId()).orderByDesc("create_time");
        //根据医生查出医院以及他的诊断申请
        Page<Disgnose_info> disgnose_infoPage = disgnoseInfoService.page(new Page<Disgnose_info>(Integer.parseInt(page), Integer.parseInt(limit)), q1);
        Hos_info hos_infoPage = hosInfoService.getOne( new QueryWrapper<Hos_info>().eq("id",doctor_info.getHos_id()));


        List<DiagnosisDoctorHos> diagnosisDoctorHosList = new ArrayList<>();

        List<Disgnose_info> disgnose_infoPageRecords = disgnose_infoPage.getRecords();

        //添加诊断申请表
        for(int i =0;i<disgnose_infoPageRecords.size();i++){
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            diagnosisDoctorHos.setDoctor_info(doctor_info);
            diagnosisDoctorHos.setHos_info(hos_infoPage); //添加医院
            diagnosisDoctorHos.setDisgnose_info(disgnose_infoPageRecords.get(i));
            diagnosisDoctorHosList.add(diagnosisDoctorHos);
        }
        Result result = new Result();
        result.setCode(200);
        result.setCount(list.size());
        result.setData(diagnosisDoctorHosList);
        return result;
    }

    /**
     * 诊断申请的组合查询
     * @return
     */
    @ResponseBody
    @GetMapping("/get_diagnosis_list_by_params_assits")
    public Result get_diagnosis_list_by_params(String disgnose_code,String patient_name,String patient_tall,String patient_weight,String diagnose_result,String create_time,String end_time,String stat,
    HttpSession seesion)
    {
        System.out.println(create_time);
        System.out.println(end_time);

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
        if(diagnose_result!=null)
        if (!StringUtils.isEmpty(diagnose_result.trim())){
            queryWrapper.like("diagnose_result",diagnose_result);
        }
        if (!StringUtils.isEmpty(create_time.trim()) && !StringUtils.isEmpty(end_time.trim())){

            Timestamp create_time1 = Timestamp.valueOf(create_time+" 00:00:00");
            Timestamp end_time1 = Timestamp.valueOf(end_time+" 00:00:00");
            long endT = end_time1.getTime()+1000*60*60*24;
            end_time1.setTime(endT);
            queryWrapper.between("create_time",create_time1,end_time1);
        }
        if (!StringUtils.isEmpty(stat.trim())){
            queryWrapper.like("stat",stat);
        }
        queryWrapper.eq("doctor_id",((Doctor_info)seesion.getAttribute("doctor_info")).getId()).orderByDesc("create_time");

        List<Disgnose_info> list = disgnoseInfoService.list(queryWrapper);

        List<DiagnosisDoctorHos> resList = new ArrayList<DiagnosisDoctorHos>();


        for (int i = 0; i < list.size(); i++)
        {
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            Disgnose_info disgnose_info = list.get(i);

            diagnosisDoctorHos.setDisgnose_info(disgnose_info);//绑定诊断信息
            Doctor_info doctorInfo = doctorInfoService.getById(disgnose_info.getDoctor_id());
            doctorInfo.setDoctor_pwd(null);
            diagnosisDoctorHos.setDoctor_info(doctorInfo);//绑定医生
            Long hos_id = doctorInfoService.getById(disgnose_info.getDoctor_id()).getHos_id();
            diagnosisDoctorHos.setHos_info(hosInfoService.getById(hos_id));//绑定医院信息

            resList.add(diagnosisDoctorHos);
        }
        Result result = new Result();
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(resList);
        result.setCode(200);
        return result;
    }

    /**
     * 组合查询
     * @param disgnose_code
     * @param patient_name
     * @param patient_tall
     * @param patient_weight
     * @param diagnose_result
     * @param create_time
     * @param stat
     * @param seesion
     * @return
     */
    @GetMapping("/get_disgnois_list_byparams_W")
    public Result get_disgnois_list_byparams_W(String disgnose_code,String patient_name,String patient_tall,String patient_weight,String diagnose_result,String create_time,String end_time,String stat,
                                               HttpSession seesion){
        //拼接条件查询
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
        if(diagnose_result!=null)
        if (!StringUtils.isEmpty(diagnose_result.trim())){
            queryWrapper.like("diagnose_result",diagnose_result);
        }
        if (!StringUtils.isEmpty(create_time.trim()) && !StringUtils.isEmpty(end_time.trim())){

            Timestamp create_time1 = Timestamp.valueOf(create_time+" 00:00:00");
            Timestamp end_time1 = Timestamp.valueOf(end_time+" 00:00:00");
            long endT = end_time1.getTime()+1000*60*60*24;
            end_time1.setTime(endT);
            queryWrapper.between("create_time",create_time1,end_time1);
        }
        if (!StringUtils.isEmpty(stat.trim())){
            queryWrapper.like("stat",stat);
        }
        queryWrapper.orderByDesc("create_time");
        //查询出当前医生的诊断任务
        Doctor_info doctor_info = (Doctor_info)seesion.getAttribute("doctor_info");
        List<Doctor_with_disgnose> dwdL = doctorWithDisgnoseService.list(new QueryWrapper<Doctor_with_disgnose>().eq("doctor_id",doctor_info.getId()).orderByDesc("create_time"));

        List<DiagnosisDoctorHos> diagnosisDoctorHosList = new ArrayList<>();
        //根据分配表查询出所有诊断申请
        for(int i = 0 ;i<dwdL.size();i++){
            Long disgnose_id = dwdL.get(i).getDisgnose_id();
            QueryWrapper<Disgnose_info> queryWrapper1 = queryWrapper.clone();
            Disgnose_info disgnose_info = disgnoseInfoService.getOne(queryWrapper1.eq("id",disgnose_id).orderByDesc("create_time"));
            queryWrapper1.clear();
            if(disgnose_info!=null){
                Doctor_info doctor_info1 = doctorInfoService.getById(disgnose_info.getDoctor_id());
                Hos_info hosInfo = hosInfoService.getById(doctor_info1.getHos_id());
                DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
                diagnosisDoctorHos.setDisgnose_info(disgnose_info);
                diagnosisDoctorHos.setDoctor_info(doctor_info1);
                diagnosisDoctorHos.setHos_info(hosInfo);
                diagnosisDoctorHosList.add(diagnosisDoctorHos);
            }
        }
        Result result = new Result();
        result.setData(diagnosisDoctorHosList);
        result.setCount(diagnosisDoctorHosList.size());
        result.setCode(200);
        return result;
    }

    /**
     * 获取当前医师被分配到的任务
     */
    @GetMapping("/getManagerDiagois")
    public Result get_manager_diagnois_list(HttpSession session,
                                            String page,
                                            String limit){
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        //查询和当前医师有关的诊断申请
        //1. 查询总数
        int count = doctorWithDisgnoseService.count(new QueryWrapper<Doctor_with_disgnose>().eq("doctor_id",doctor_info.getId()));
        //2. 分页查询
        IPage<Doctor_with_disgnose> doctor_with_disgnosePage = new Page<Doctor_with_disgnose>(Integer.parseInt(page), Integer.parseInt(limit));
        QueryWrapper<Doctor_with_disgnose> q1 = new QueryWrapper<Doctor_with_disgnose>().eq("doctor_id", doctor_info.getId()).orderByDesc("create_time");
        IPage<Doctor_with_disgnose> dwdPage = doctorWithDisgnoseService.page(doctor_with_disgnosePage, q1);
        List<Doctor_with_disgnose> dwdR = dwdPage.getRecords();//dwd数据

        List<DiagnosisDoctorHos> diagnosisDoctorHosList = new ArrayList<>();
        //根据分配表查询出所有诊断申请
        for(int i = 0;i<dwdR.size();i++){
            Long disgnose_id = dwdR.get(i).getDisgnose_id();
            Disgnose_info disgnose_info = disgnoseInfoService.getById(disgnose_id); //诊断申请
            //根据诊断申请查出医师
            Doctor_info newDoctor = doctorInfoService.getOne(new QueryWrapper<Doctor_info>().eq("id", disgnose_info.getDoctor_id()));
            //根据医师查出医院
            Hos_info hosInfo = hosInfoService.getOne(new QueryWrapper<Hos_info>().eq("id",newDoctor.getHos_id()));
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();//组合关系
            diagnosisDoctorHos.setHos_info(hosInfo);
            diagnosisDoctorHos.setDoctor_info(newDoctor);
            diagnosisDoctorHos.setDisgnose_info(disgnose_info);
            diagnosisDoctorHosList.add(diagnosisDoctorHos);
        }
        //返回结果
        Result result = new Result();
        result.setMsg("成功");
        result.setCode(200);
        result.setCount(count);
        result.setData(diagnosisDoctorHosList);
        return result;
    }

    @GetMapping("/getTempDisgnose_info")
    public Result getTempDisgnose_info(HttpSession session){
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        Disgnose_info disgnose_info = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("stat", 0).eq("doctor_id",doctor_info.getId()));
        Result result = new Result();
        result.setData(disgnose_info);
        return result;
    }

    @GetMapping("/getDisgnose_InfoById")
    public Result getDisgnose_InfoById(String id){
        Result result = new Result();
        Disgnose_info byId = disgnoseInfoService.getById(id);
        result.setData(byId);
        return result;
    }

    /**
     * new 需求，删除临时表
     * @param id
     * @param session
     * @return
     */
    @GetMapping("/deleteTempDisgnose")
    public Result deleteTempDisgnose(String id,HttpSession session){
        Result result = new Result();
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        Disgnose_info disgnose_code = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("id", id));

        //判断用户和的id和诊断表中id一一对应
        if(disgnose_code !=null && doctor_info!=null&&doctor_info.getId().equals(disgnose_code.getDoctor_id())){
            //是否临时表
            if(disgnose_code.getStat() == 0) {
                boolean b = false;
                //删除文件
                List<Attached> attacheds = attachedService.list(new QueryWrapper<Attached>().eq("disgnose_id", disgnose_code.getId()));
                for (Attached attached:
                     attacheds) {
                   File file = new File(attached.getAttached_addr()+attached.getAttched_name());
                   if(file.exists()){//如果存在删除
                       file.delete();
                   }
                   attached.deleteById();//删除文件
                }
                //删除表
                 b = disgnose_code.deleteById();
                if(b){
                    result.setCode(200);
                    result.setMsg("删除成功");
                }
                else{
                    result.setCode(500);
                    result.setMsg("由于网络原因，删除失败");
                }
            }else{
                result.setCode(200);
                result.setMsg("非临时表，无法进行删除!");
            }
        }else{ //请先登录或者确认改临时表是否存在
            result.setCode(500);
            result.setMsg("请先去人该诊断是否存在，或者用户是否登录");
        }

        return result;
    }


}

