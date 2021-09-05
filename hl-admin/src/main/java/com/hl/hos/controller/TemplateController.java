package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DoctorFile;
import com.hl.hos.pojo.*;
import com.hl.hos.service.Doctor_infoService;
import com.hl.hos.service.TemplateService;
import com.hl.hos.utils.DateUtil;
import com.hl.hos.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 何夜息
 * @since 2021-09-01
 */
@RestController
@RequestMapping("/hos/template")
public class TemplateController {
    @Autowired
    private Result result;

    @Value("${diagnosis.filePath}")
    private String savePath;
    @Autowired
    private Template template;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private Doctor_infoService doctor_infoService;

    /**
     * 文件上传接口
     * @param file
     * @param request
     * @param session
     * @return
     * @throws IOException
     */
    @PostMapping("/fileUpload")
    @ResponseBody
    public Result fileUpload(MultipartFile file,
                             HttpServletRequest request,
                             HttpSession session) throws IOException
    {
        String fileName = "";
        if(file.isEmpty()){
            result.setCode(500);
            result.setMsg("文件上传异常");
        }else{
            //如果上传的文件不为空
            fileName = FileNameUtils.getFileName(file.getOriginalFilename());//获取文件名称

            File files = new File(savePath+fileName);
            if(!files.getParentFile().exists()){
                files.mkdirs() ; //如果父目录不存在创建
            }
            try{
                //上传成功
                file.transferTo(files);

                Doctor_info doctor_info = (Doctor_info)session.getAttribute("doctor_info");

                //将文件信息保存到附件表当中
                template.setFile_addr(savePath);
                template.setCreat_time(DateUtil.getNowStringDateTime());
                template.setType("模板");
                template.setFile_name(fileName);
                if(doctor_info!=null)
                    template.setDoctor_id(doctor_info.getId());
                templateService.save(template);
            }catch (Exception e){
                e.printStackTrace();
            }
            result.setCode(200);
            result.setMsg("成功");
        }
        return result;
    }


    /**
     * 根据分页获取文件列表
     */
    @GetMapping("/file_list")
    @ResponseBody
    public Result file_list(String page,String limit)
    {
        List<Template> list = templateService.list();

        List<DoctorFile> res = new ArrayList<>();

        Page<Template> page1 = new Page<>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Template> iPage = templateService.page(page1);

        for (int i = 0; i < iPage.getRecords().size(); i++)
        {
            Template template1 = iPage.getRecords().get(i);
            QueryWrapper<Doctor_info> doctor_infoQueryWrapper = new QueryWrapper<>();
            doctor_infoQueryWrapper.select("doctor_name","doctor_account");
            doctor_infoQueryWrapper.eq("id",template1.getDoctor_id());
            Doctor_info doctorInfo = doctor_infoService.getOne(doctor_infoQueryWrapper);

            DoctorFile doctorFile = new DoctorFile();
            doctorFile.setDoctor_info(doctorInfo);
            doctorFile.setTemplate(template1);
            res.add(doctorFile);
        }
        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(res);
        result.setCode(200);
        return result;
    }

    /**
     * 根据文件名查询附件
     */
    @GetMapping("/get_file_by_name")
    @ResponseBody
    public Result get_file_by_name(String file_name)
    {
        QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("file_name",file_name);
        List<Template> res = templateService.list(queryWrapper);
        result.setCount(res.size());//数量应该是所有数据的大小
        result.setData(res);
        result.setMsg("成功成功");
        result.setCode(200);
        return result;
    }

    /**
     * 根据id删除末班文件，物理删除
     * @param file_name
     * @return
     */
    @GetMapping("/delete_file_by_name")
    public Result delete_file_by_id(String file_name){
        Result result = new Result();
        //根据id查询模板文件
        Template temByid = templateService.getOne(new QueryWrapper<Template>().eq("file_name",file_name));
        if(temByid == null) {
            result.setCode(500);
            result.setMsg("文件不存在");

        }else{
            String filePath = temByid.getFile_addr()+temByid.getFile_name();
            File file = new File(filePath);
            if(file.delete()){
                //删除数据库的记录
                boolean b = temByid.deleteById();
                if(b){
                    result.setMsg("文件删除成功");
                    result.setCode(200);
                }
            }else{
                result.setCode(500);
                result.setMsg("文件删除失败");
            }
        }

        return result;
    }
    @GetMapping("/download_template_file_by_name")
    @ResponseBody()
    public Result   downLoadFileByFileName(HttpServletRequest request, HttpServletResponse response, String fileName) {
        Result result = new Result();
        if (fileName != null) {

            //先去数据库中查找是否有此文件
            Template attachedByFileName = templateService.getOne(new QueryWrapper<Template>().eq("file_name", fileName));
            Template attachedResultByFileName = templateService.getOne(new QueryWrapper<Template>().eq("file_name",fileName));
            if(attachedByFileName == null && attachedResultByFileName == null){ //两个文件都为null
                result.setCode(500);
                result.setMsg("文件不存在");
                return result;
            }
            //设置文件路径
            File file = new File(savePath+fileName);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
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
            }else{

            }
        }
        result.setCode(500);
        result.setMsg("下载出错了！");
        return result;
    }
}

