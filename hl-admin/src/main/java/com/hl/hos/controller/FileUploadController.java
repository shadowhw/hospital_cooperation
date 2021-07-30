package com.hl.hos.controller;

import com.hl.hos.pojo.Attached;
import com.hl.hos.pojo.Disgnose_info;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.AttachedService;
import com.hl.hos.service.Disgnose_infoService;
import com.hl.hos.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileUploadController {
    @Value("${diagnosis.filePath}")
    private String savePath;
    @Autowired
    private Disgnose_infoService disgnoseInfoService; //诊断申请表
    @Autowired
    private AttachedService attachedService;//诊断申请附件
    //论文的Id集合
    private List<Attached> fileslist = new ArrayList<>();
    private Doctor_info doctor_info;
    //文件上传接口
    @PostMapping("/fileUpload")
    @ResponseBody
    public Result fileUpload(MultipartFile file,
                             HttpServletRequest request,
                             HttpSession session) throws IOException {
        Result result = new Result();

        String fileName = "";
        if(file.isEmpty()){
            result.setCode(500);
            result.setMsg("文件上传异常");
        }else{
            //如果上传的文件不为空不为空
            fileName = FileNameUtils.getFileName(file.getOriginalFilename());//获取文件名称
            System.out.println(savePath);
            File files = new File(savePath+fileName);
            if(!files.getParentFile().exists()){
                files.mkdirs() ; //如果父目录不存在创建
            }
            try{
                //上传成功
                file.transferTo(files);
                //将文件信息保存到附件表当中
                Attached attached = new Attached();
                attached.setAttached_addr(savePath);
                attached.setAttched_name(fileName);
                attached.setDoctor_id(doctor_info.getId());
                attachedService.save(attached);
                fileslist.add(attached);//加入进集合 便于绑定

            }catch (Exception e){
                e.printStackTrace();
            }


            result.setCode(0);
            result.setMsg("成功");
        }
       return result;
    }

    @PostMapping("/saveDisgnose_info")
    @ResponseBody
    public Result saveDisa(String patient_name,
                           String patient_birth,
                           String patient_tall,
                           String patient_weight,
                           String department,
                           String bz){

        Disgnose_info disgnose_info = new Disgnose_info();
        Result result = new Result();
        disgnose_info.setPatient_name(patient_name);
        disgnose_info.setPatient_tall(patient_tall);
        disgnose_info.setPatient_birth(Timestamp.valueOf(patient_birth+" 00:00:00")); //兼容timestamp
        disgnose_info.setPatient_weight(patient_weight);
        disgnose_info.setDepartment(department);
        disgnose_info.setComment_text(bz);
        disgnose_info.setDoctor_id(doctor_info.getId());
        disgnose_info.setStat(1);
        //讲诊断信息保存到数据库
        boolean save = disgnoseInfoService.save(disgnose_info);
        System.out.println(disgnose_info);
        if(save){ //保存成功
            try {
                //附件表更新，与诊断申请绑定
                for (int i = 0; i < fileslist.size(); i++) {
                    Attached attached = fileslist.get(i);
                    attached.setDisgnose_id(disgnose_info.getId());
                    attachedService.updateById(attached);
                }
                result.setMsg("success");
            }catch (Exception e){
                e.printStackTrace();
                result.setMsg("error");
            }
        }else{
            result.setMsg("error");
        }
        return result;
    }

    //去到诊断上
    @GetMapping("/goUpload")
    public String goUpload(){
        return "redirect:/pages/assistDoctors/assist_upload.html";
    }
    //诊断申请页面中获取医师信息
    @GetMapping("/getInfo")
    @ResponseBody
    public Doctor_info getInfo(HttpSession httpSession){
        doctor_info = (Doctor_info)httpSession.getAttribute("doctor_info");
        return doctor_info;
    }
}

