package com.hl.hos.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DoctorHos;
import com.hl.hos.pojo.Disgnose_info;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Disgnose_infoService;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.Doctor_with_disgnoseService;
import com.hl.hos.service.Hos_infoService;
import com.hl.hos.service.impl.MailServiceImpl;
import com.hl.hos.utils.DateUtil;
import com.hl.hos.utils.MD5Util;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
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
    private Disgnose_infoService disgnose_infoService;
    @Autowired
    private Result result;
    @Autowired
    private Doctor_with_disgnoseService doctor_with_disgnoseService;
    @Autowired
    MailServiceImpl mailService; //邮件

    /**
     * 根据页码和状态查询所有医生信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_doctor_list")
    public Result get_doctor_list(String page,String limit,int type,int pass)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<>();//排除协助医师
        //协助医师
        if(type==1)
        {
            queryWrapper.in("stat",1,3);
        }
        //普通医师
        if(type==2)
        {
            queryWrapper.eq("pass",pass);
            queryWrapper.in("stat",2,4);
        }

        //查询医生总数
        List<Doctor_info> tem_list = doctor_infoService.list(queryWrapper);
        List<DoctorHos> doctorLists = doctor_infoService.get_hos_doc_page(queryWrapper,page,limit);
        result.setCount(tem_list.size());//数量应该是所有数据的大小
        result.setData(doctorLists);
        result.setCode(200);
        return result;
    }

    @Value("${spring.mail.username}")
    private String sendServiceMail ;


    /**
     * 更新医生信息
     * @return
     */
    @ResponseBody
    @GetMapping("/update_doctor_info")
    public Result update_doctor_info(Doctor_info doctor_info,@Nullable String hos_addr,@Nullable String hos_name)
    {
        if(doctor_info.getDoctor_pwd()!=null)
            doctor_info.setDoctor_pwd(MD5Util.getMd5(doctor_info.getDoctor_pwd()));
        //如果是删除直接删除
        if(doctor_info.getStat()!=null && doctor_info.getStat()==8)
        {
            //获取身份
            Doctor_info tem_doc = doctor_infoService.getById(doctor_info.getId());
            HashMap<String,Object> map = new HashMap<>();
            //协作医师删除协作记录
            if(tem_doc.getStat()==1)
            {
                map.put("doctor_id",tem_doc.getId());
                doctor_with_disgnoseService.removeByMap(map);
            }

            //普通医师删除上传的申请和和有关该诊断表的协助信息
            if(tem_doc.getStat()==2)
            {
                map.put("doctor_id",tem_doc.getId());

                HashMap<String,Object> t = new HashMap<>();
                //获取该医师的所有诊断记录
                List<Disgnose_info> list = disgnose_infoService.list(new QueryWrapper<Disgnose_info>()
                    .eq("doctor_id",doctor_info.getId())
                );
                if(list.size()>=1)
                {
                    //删除协助表
                    for (int i = 0; i < list.size(); i++)
                    {
                        Disgnose_info t_dis = list.get(i);
                        t.put("disgnose_id",t_dis.getId());
                        doctor_with_disgnoseService.removeByMap(t);
                    }
                }
                disgnose_infoService.removeByMap(map);
            }
            doctor_infoService.removeById(doctor_info.getId());
            result.setCode(200);
            result.setData(null);
            result.setMsg("删除成功");
            return result;
        }
        if(doctor_infoService.updateById(doctor_info))
        {
            //根据Id查找该医生
            Doctor_info byId = doctor_infoService.getById(doctor_info.getId());
            //通过后更改医院状态为通过
            if(byId.getPass()==1)
            {
                //通过
                //QueryWrapper<Hos_info> queryWrapper = new QueryWrapper<>();
                //queryWrapper.eq("hos_addr",hos_addr).eq("hos_name",hos_name);
                Hos_info hos_info = hos_infoService.getById(byId.getHos_id());
                if(hos_info.getStat()==2)
                {
                    hos_info.setStat(1);
                    hos_infoService.saveOrUpdate(hos_info);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mailService.sendMineEmail(
                                sendServiceMail
                                ,byId.getEmail()
                                ,sendServiceMail
                                ,"国家结构性心脏病介入质控中心影像评估核心实验室审核通知"
                                ,"您的注册申请已审核通过，您现在可以登录您的账号了!"+"<a href="+"http://150.223.27.2:81"+"/>点击前往登录<a/>"
                        );
                    }
                }).start();
            }else { //未通过
                //分出线程去发送邮箱
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mailService.sendSimpleMail(
                                    sendServiceMail
                                    ,byId.getEmail()
                                    ,sendServiceMail
                                    ,"国家结构性心脏病介入质控中心影像评估核心实验室审核通知"
                                    ,"您的注册申请未审核通过!!");
                        }catch (Exception exception){

                        }
                    }
                }).start();


            }
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
     * 批量导入医生信息
     * @return
     */
    @ResponseBody
    @PostMapping("/batch_add_doc")
    public Result batch_add_doc(@RequestBody String doc_infos)
    {
        String decode = URLDecoder.decode(doc_infos);
        JSONArray jsonArray = JSONArray.parseArray(decode.substring(10));
        String no_hos_doc = "";//没有地址的医生不入库
        for (int i = 0; i < jsonArray.size(); i++)
        {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            if(jsonObject.getString("hos_name").equals("")
                || jsonObject.getString("hos_name")==null
                || jsonObject.getString("hos_addr").equals("")
                || jsonObject.getString("hos_addr")==null
            )
            {
                no_hos_doc += jsonObject.getString("doctor_name")+",";
                continue;
            }
            //查询医院:不存在就新增
            Hos_info hos_info = hos_infoService.getOne(new QueryWrapper<Hos_info>()
                .eq("hos_name",jsonObject.getString("hos_name"))
                .eq("hos_addr",jsonObject.getString("hos_addr"))
            );
            if(hos_info==null)
            {
                hos_info = new Hos_info();
                hos_info.setStat(1);
                hos_info.setComment_text("批量添加医生时添加医院");
                hos_info.setCreate_time(DateUtil.getNowSqlDateTime());
                hos_info.setHos_addr(jsonObject.getString("hos_addr"));
                hos_info.setHos_name(jsonObject.getString("hos_name"));
                hos_infoService.save(hos_info);
            }
            //查询医生是否已经添加
            Doctor_info doctorInfo = doctor_infoService.getOne(new QueryWrapper<Doctor_info>()
                .eq("doctor_name",jsonObject.getString("doctor_name"))
                .eq("doctor_zuoji",jsonObject.getString("doctor_zuoji"))
                .eq("stat",1)
                .eq("doctor_tel",jsonObject.getString("doctor_tel"))
                .eq("email",jsonObject.getString("email"))
            );
            if(doctorInfo!=null)
                continue;//存在不需要添加
            else
                doctorInfo = new Doctor_info();
            doctorInfo.setStat(1);//协作医生
            doctorInfo.setDoctor_pwd(MD5Util.getMd5("123456"));
            //设置医生账号：医院名加该医院数量
            List<Doctor_info> hos_doctors = doctor_infoService.list(new QueryWrapper<Doctor_info>()
                    .eq("hos_id",hos_info.getId())
            );
            String accountName = hos_doctors.size() < 9 ? hos_info.getHos_name()+"E"+"0"+(hos_doctors.size()+1) : hos_info.getHos_name()+"E"+(hos_doctors.size()+1);
            doctorInfo.setDoctor_account(accountName);
            doctorInfo.setHos_id(hos_info.getId());
            doctorInfo.setDoctor_tel(jsonObject.getString("doctor_tel"));
            doctorInfo.setPass(1);
            doctorInfo.setCreate_time(DateUtil.getNowSqlDateTime());
            doctorInfo.setComment_text("批量导入协作医生");
            doctorInfo.setDoctor_addr(jsonObject.getString("doctor_addr"));
            doctorInfo.setDoctor_name(jsonObject.getString("doctor_name"));
            doctorInfo.setDoctor_zuoji(jsonObject.getString("doctor_zuoji"));
            doctorInfo.setEmail(jsonObject.getString("email"));
            doctor_infoService.save(doctorInfo);
        }

        result.setMsg("添加成功");
        result.setData(no_hos_doc);
        result.setCode(200);
        return result;
    }

    /**
     * 根据医院查询所属医院下的所有合法医生
     * @return
     */
    @ResponseBody
    @GetMapping("/get_doc_of_hos")
    public Result get_doc_of_hos(Long hos_id,String type)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<Doctor_info>();
        queryWrapper.select("id","doctor_name","doctor_account").eq("hos_id",hos_id).eq("pass",1);

        if(type.equals("xz"))//查询协助医师
            queryWrapper.eq("stat",1);//协作医师
        else if(type.equals("pt"))
            queryWrapper.eq("stat",2);//普通医师
        else
            queryWrapper.in("stat",0,1,2);//所以账号正常的账号
        List<Doctor_info> doc_list = doctor_infoService.list(queryWrapper);
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
    public Result get_doctor_list_by_params(@Nullable String page,@Nullable String limit,String doctor_name,String email,String doctor_tel,String doctor_account,String pass,int type)
    {
        QueryWrapper<Doctor_info> queryWrapper = new QueryWrapper<>();
        //协助医师
        if(type==1)
        {
            queryWrapper.in("stat",1,3);
        }
        //普通医师
        if(type==2)
        {
            queryWrapper.in("stat",2,4);
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

        //分页查询数据
        List<DoctorHos> doctorLists = doctor_infoService.get_hos_doc_page(queryWrapper,page,limit);

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
        doctor_info.setDoctor_pwd(MD5Util.getMd5("123456"));//默认密码为123456
        //设置医生账号：医院名加该医院数量
        List<Doctor_info> hos_doctors = doctor_infoService.list(new QueryWrapper<Doctor_info>()
                .eq("hos_id",hos_id)
        );
        String accountName = "";
        if(hos_doctors.size() <9){ //小于10添加0
             accountName = hos_info.getHos_name()+"E"+"0"+(hos_doctors.size()+1);
        }else{
            accountName = hos_info.getHos_name()+"E"+(hos_doctors.size()+1);
        }

        doctor_info.setDoctor_account(accountName);
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

    /**
     * 获取医师和医院信息
     * @return
     */
    @PostMapping("/getDoctorWithHosInfo")
    @ResponseBody
    public Result getDoctorWithHosInfo(HttpSession session){
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        //查找出医院信息
        Hos_info hostInfo = hos_infoService.getById(doctor_info.getHos_id());
        DoctorHos doctorHos = new DoctorHos();
        doctorHos.setDoctor_info(doctor_info);
        doctorHos.setHos_info(hostInfo);

        Result result = new Result();
        result.setData(doctorHos);
        return result;
    }

    /**
     * 个人信息更新
     */
    @PostMapping("/updateDoctorInfo")
    public Result updateDoctorInfo(Doctor_info doctor_info,HttpSession session){
        Doctor_info d = (Doctor_info)session.getAttribute("doctor_info");
        long id = d.getId();
        doctor_info.setId(id);
        doctor_info.updateById();

        //查询数据库更新session
        Doctor_info byId = doctor_infoService.getById(id);

        //更新seession
        session.setAttribute("doctor_info",byId);
        return null;
    }
}

