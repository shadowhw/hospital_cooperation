package com.hl.hos.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.pojo.*;
import com.hl.hos.service.AttachedService;
import com.hl.hos.service.Attached_resultService;
import com.hl.hos.service.Disgnose_infoService;
import com.hl.hos.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
public class FileUploadController {
    @Value("${diagnosis.filePath}")
    private String savePath;
    @Autowired
    private Disgnose_infoService disgnoseInfoService; //诊断申请表
    @Autowired
    private AttachedService attachedService;//诊断申请附件
    @Autowired
    private Attached_resultService attachedResultService;

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
                attached.setCreate_time(Timestamp.valueOf(LocalDateTime.now()));
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


    /**
     * 临时保存
     * @param patient_name
     * @param patient_birth
     * @param patient_tall
     * @param patient_weight
     * @param department
     * @param bz
     * @return
     */
    @PostMapping("/saveTempDisgnose_info")
    @ResponseBody
    public Result saveTempDisgnose(String id ,
                                    String patient_name,
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
        disgnose_info.setStat(0); //只是临时保存，不需要提交

        boolean save = false;
        if(id == null|| "".equals(id)){ //并没有该记录临时
             save = disgnoseInfoService.save(disgnose_info);
        }else{
            disgnose_info.setId(Long.valueOf(id));
            save = disgnoseInfoService.updateById(disgnose_info);
        }
        //讲诊断信息保存到数据库

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
        fileslist.clear(); //清除
        return result;
    }

    /**
     * 上传提交诊断申请
     * @param patient_name
     * @param patient_birth
     * @param patient_tall
     * @param patient_weight
     * @param department
     * @param bz
     * @return
     */
    @PostMapping("/saveDisgnose_info")
    @ResponseBody
    public Result saveDisa(String id,
                            String patient_name,
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
        disgnose_info.setStat(1); //查看是否有临时的
        disgnose_info.setCreate_time(Timestamp.valueOf(LocalDateTime.now()));
        disgnose_info.setDisgnose_code(new SimpleDateFormat("yyyyMMdd").format(new Date())+ UUID.randomUUID().toString().substring(5,8));
        boolean b = false;
       if(id == null ||"".equals(id)){ //不存在临时记录
            b = disgnose_info.insert(); //直接保存
       }else{
           disgnose_info.setId(Long.valueOf(id));
           b = disgnose_info.updateById();
       }


        if(b){ //保存成功
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
        fileslist.clear(); //清除
        return result;
    }

    @GetMapping("/getDisnosisByCode")
    @ResponseBody
    public Result getDisnosisByCode(String disgnose_code){//根据诊断编号获取信息
        Disgnose_info disgnoseInfoByCode = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("disgnose_code", disgnose_code));
        Result result = new Result();
        result.setCode(200);
        result.setData(disgnoseInfoByCode);
        return result;
    }
    /**
     * 重新上传诊断申请
     * @param patient_name
     * @param patient_birth
     * @param patient_tall
     * @param patient_weight
     * @param department
     * @param bz
     * @param disgnose_code
     * @return
     */
    @RequestMapping("/reUploadRequestDisnosis")
    @ResponseBody
    public Result reUploadRequestDisnosis(String patient_name,
                                          String patient_birth,
                                          String patient_tall,
                                          String patient_weight,
                                          String department,
                                          String bz,
                                          String disgnose_code,
                                          HttpSession session){
        //更新诊断信息
        Disgnose_info disgnoseInfoByCode = disgnoseInfoService.getOne(new QueryWrapper<Disgnose_info>().eq("disgnose_code", disgnose_code));
        Result result = new Result();
        Doctor_info doctor_info = (Doctor_info) session.getAttribute("doctor_info");
        if(disgnoseInfoByCode == null){
            result.setMsg("error");
            result.setCode(500);
        }else{
            disgnoseInfoByCode.setPatient_name(patient_name);
            disgnoseInfoByCode.setPatient_birth(Timestamp.valueOf(patient_birth+" 00:00:00"));
            disgnoseInfoByCode.setPatient_tall(patient_tall);
            disgnoseInfoByCode.setPatient_weight(patient_weight);
            disgnoseInfoByCode.setDepartment(department);
            disgnoseInfoByCode.setComment_text(bz);
            disgnoseInfoByCode.setStat(1);//设置为初始值
            disgnoseInfoService.updateById(disgnoseInfoByCode); //更新表

            if(!fileslist.isEmpty()){
                for(int i = 0;i< fileslist.size();i++){
                    Attached attached = fileslist.get(i);
                    attached.setDisgnose_id(disgnoseInfoByCode.getId()); //附件绑定
                    attachedService.updateById(attached);
                }
            }
            result.setCode(200);
            result.setMsg("success");
            result.setData( disgnoseInfoByCode);
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


    @GetMapping("/downLoadFileByFileName")
    @ResponseBody()
    public Result   downLoadFileByFileName(HttpServletRequest request, HttpServletResponse response,String fileName) {
        Result result = new Result();
        if (fileName != null) {

            //先去数据库中查找是否有此文件
            Attached attachedByFileName = attachedService.getOne(new QueryWrapper<Attached>().eq("attched_name", fileName));
            Attached_result attachedResultByFileName = attachedResultService.getOne(new QueryWrapper<Attached_result>().eq("attched_name",fileName));
            if(attachedByFileName == null && attachedResultByFileName == null){ //两个文件都为null
                result.setCode(500);
                result.setMsg("文件不存在");
                return result;
            }
            //设置文件路径
            File file = new File(savePath+fileName);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
//                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    result.setCode(200);
                    result.setMsg("下载成功");
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally { // 做关闭操作
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        result.setCode(500);
        result.setMsg("下载出错了！");
        return result;
    }
}

