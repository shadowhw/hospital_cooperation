package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.hl.hos.backPogo.AttachedWithResutAttached;
import com.hl.hos.pojo.*;
import com.hl.hos.service.AttachedService;
import com.hl.hos.service.Attached_resultService;
import com.hl.hos.service.Disgnose_infoService;
import com.hl.hos.service.Hos_infoService;
import com.hl.hos.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  诊断结果
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-05
 */
@RestController
public class Disnose_resultController {
    @Autowired
    private Attached_resultService attachedResultServices;
    @Autowired
    private Disgnose_infoService disgnoseInfoService;
    @Autowired
    private Hos_infoService hosInfoService;
    @Autowired
    AttachedService attachedService;

    @Deprecated
    private Doctor_info doctor_info;



    @Value("${diagnosis.filePath}")
    private String savePath;

    /**
     * 诊断结果附件上传
     */
    @PostMapping("/fileResultUpload")
    public Result fileResultUpload(MultipartFile file , HttpSession session){
        Result result = new Result();

        if(!file.isEmpty()){
            //获取文件名称
            String originalFilename = file.getOriginalFilename();
            String fileName = FileNameUtils.getFileName(originalFilename);
            File files = new File(savePath+fileName);
            if(files.getParent()== null)
                files.mkdirs();
            try {
                file.transferTo(files);
                //上传成功，保存文件信息
                Attached_result attachedResult = new Attached_result();
                attachedResult.setAttached_addr(savePath);
                attachedResult.setAttched_name(fileName);
                attachedResult.setCreate_time(Timestamp.valueOf(LocalDateTime.now()));
                attachedResult.setDoctor_id(  ((Doctor_info) session.getAttribute("doctor_info")).getId());
                attachedResultServices.save(attachedResult);
                //返回id
                result.setAttachedId(attachedResult.getId()+"");
                result.setData(attachedResult.getAttched_name());
            }catch (Exception e){

            }
            result.setCode(0);
            result.setMsg("成功");
        }else{
            result.setCode(500);
        }
        return result;
    }

    /**
     * 诊断结果回执
     * @return
     */
    @PostMapping("/disgnoseResuletBack")
    public Result disgnoseResuletBack(String code,String disgnosResult,
                                      String[] attchedIds, HttpSession session){
        doctor_info = (Doctor_info) session.getAttribute("doctor_info");
        Result result = new Result();
        //根据诊断编号查出诊断信息
        Disgnose_info disgnose_info = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("disgnose_code", code));
        disgnose_info.setDiagnose_result(disgnosResult);
        //诊断完成
        disgnose_info.setStat(6);

        //讲结果附件绑定在申请表当中
        if(attchedIds!=null) {
            for(int i = 0;i<attchedIds.length;i++){
                Attached_result attached_result = new Attached_result();
                attached_result = attached_result.selectById(attchedIds[i]);
                attached_result.setDisnose_result_id(disgnose_info.getId());
                attached_result.updateById();
            }
        }

        disgnoseInfoService.updateById(disgnose_info);
        result.setMsg("success");
        result.setCode(200);
        result.setData(disgnose_info);
        result.setUserName(((Doctor_info) session.getAttribute("doctor_info")).getDoctor_name());
        return result;
    }

    /**
     *
     * @param comm_text: 回退原因
     * @param code: 诊断编号
     * @return
     */
    @PostMapping("/disnosis_roll_back")
    public Result disnosisRollBack(String comm_text,String code,HttpSession session){
        //根据诊断标号查询诊断申请，
        Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");
        String doctor_name = ((Doctor_info) session.getAttribute("doctor_info")).getDoctor_name();
        Disgnose_info disgnoseInfoByCode = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("disgnose_code", code));
        Result result = new Result();
        if(disgnoseInfoByCode == null){
            result.setMsg("error");
        }else{
            disgnoseInfoByCode.setComment_text(comm_text);
            disgnoseInfoByCode.setStat(2);//退回
            disgnoseInfoService.updateById(disgnoseInfoByCode);
            result.setMsg("success");
            result.setData(disgnoseInfoByCode);
            result.setUserName(doctor_name);
        }
        return result;


    }

    /**
     * 获取结果诊断表和请求诊断表
     * @param disgnose_id
     * @return
     */
    @GetMapping("/getAttchedAndResutAttched")
    public Result getAttchedAndResultAttched(String disgnose_id){
        //根据disgnoseId获取文件信息
        List<Attached_result> resultFiles = attachedResultServices.list(new QueryWrapper<Attached_result>().eq("disnose_result_id", disgnose_id));
        List<Attached> requestFiles = attachedService.list(new QueryWrapper<Attached>().eq("disgnose_id", disgnose_id));

        List<AttachedWithResutAttached> attachedWithResutAttachedList = new ArrayList<>();
        int max = Math.max(requestFiles.size(), resultFiles.size());

        for(int i = 0 ; i<max;i++){

            if(!requestFiles.isEmpty()&&i<requestFiles.size()&&requestFiles.get(i)!=null){
                AttachedWithResutAttached attachedWithResutAttached = new AttachedWithResutAttached();
                attachedWithResutAttached.setAttached(requestFiles.get(i));
                attachedWithResutAttached.setType("申请附件");
                attachedWithResutAttachedList.add(attachedWithResutAttached);
            }

            if(!resultFiles.isEmpty()&&resultFiles.size()>i&&resultFiles.get(i)!=null){
                AttachedWithResutAttached attachedWithResutAttached = new AttachedWithResutAttached();
                Attached_result attached_result = resultFiles.get(i);
                Attached attached = new Attached();
                attached.setDisgnose_id(attached_result.getDisnose_result_id());
                attached.setId(attached_result.getId());
                attached.setAttached_addr(attached_result.getAttached_addr());
                attached.setDoctor_id(attached_result.getDoctor_id());
                attached.setAttched_name(attached_result.getAttched_name());
                attached.setStat(attached_result.getStat());
                attached.setCreate_time(attached_result.getCreate_time());

                attachedWithResutAttached.setType("结果附件");
                attachedWithResutAttached.setAttached(attached);
                attachedWithResutAttachedList.add(attachedWithResutAttached);
            }

        }
        Result result = new Result();
        result.setData(attachedWithResutAttachedList);
        result.setCode(0);
        result.setCount(attachedWithResutAttachedList.size());
        return result;
    }
}

