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

    private Doctor_info doctor_info;

    private List<Attached_result> attachedResultList = new ArrayList<>();

    @Value("${diagnosis.filePath}")
    private String savePath;
    /**
     * 诊断结果附件上传
     */
    @PostMapping("/fileResultUpload")
    public Result fileResultUpload(MultipartFile file , HttpSession session){
        Result result = new Result();
        doctor_info = (Doctor_info) session.getAttribute("doctor_info");
        if(!file.isEmpty()){
            //获取文件名称
            String originalFilename = file.getOriginalFilename();
            String fileName = FileNameUtils.getFileName(originalFilename);
            File files = new File(savePath+fileName);
            if(files.getParent()== null)
                files.mkdirs();
            try {
                file.transferTo(files); //开始上传
                //上传成功，保存文件信息
                Attached_result attachedResult = new Attached_result();
                attachedResult.setAttached_addr(savePath);
                attachedResult.setAttched_name(fileName);
                attachedResult.setCreate_time(Timestamp.valueOf(LocalDateTime.now()));
                attachedResult.setDoctor_id(doctor_info.getId());
                attachedResultServices.save(attachedResult);

                attachedResultList.add(attachedResult);
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
    public Result disgnoseResuletBack(String code,String disgnosResult,HttpSession session){
        doctor_info = (Doctor_info) session.getAttribute("doctor_info");
        Result result = new Result();
        //根据诊断编号查出诊断信息
        Disgnose_info disgnose_info = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("disgnose_code", code));
        disgnose_info.setDiagnose_result(disgnosResult);
        disgnose_info.setStat(5);//诊断完成
        //讲结果附件绑定在申请表当中
        for(int i = 0;i<attachedResultList.size();i++){
            Attached_result attached_result = attachedResultList.get(i);
            attached_result.setDisnose_result_id(disgnose_info.getId());
            attachedResultServices.updateById(attached_result);
        }
        disgnoseInfoService.updateById(disgnose_info);
        attachedResultList.clear();
        result.setMsg("success");
        result.setCode(200);
        result.setData(disgnose_info);
        result.setUserName(doctor_info.getDoctor_name());
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

