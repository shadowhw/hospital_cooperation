package com.hl.hos.controller;


import com.hl.hos.pojo.Attached;
import com.hl.hos.pojo.Attached_result;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.AttachedService;
import com.hl.hos.service.Attached_resultService;
import com.hl.hos.utils.FileNameUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author JaneOnly
 * @date 2022年02月16日 17:16
 */
@Controller
public class SplitFileUploadCntroller {
    private static final String utf8 = "UTF8";
    @Autowired
    private AttachedService attachedService;
    @Autowired
    private Attached_resultService attachedResultService;

    @Value("${diagnosis.filePath}")
    private String up;

    /**
     * 诊断申请附件
     * @param response
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("/file_request_upload")
    @ResponseBody
    public Result fileUploadBySplit(HttpServletResponse response, HttpServletRequest request, HttpSession session){
        Result result = new Result();
        //分片
        Attached attached = null;
        response.setCharacterEncoding(utf8);
        Integer schunk = null;
        Integer schunks = null;
        String name = null;
        BufferedOutputStream os = null;
        String uploadPath = up;
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            factory.setRepository(new File(uploadPath));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(5L * 1024L * 1024L * 1024L);
            upload.setSizeMax(10L * 1024L * 1024L * 1024L);
            List<FileItem> items = upload.parseRequest(request);

            for(FileItem item : items){
                if(item.isFormField()){
                    if("chunk".equals(item.getFieldName())){
                        schunk = Integer.parseInt(item.getString(utf8));
                    }
                    if("chunks".equals(item.getFieldName())){
                        schunks = Integer.parseInt(item.getString(utf8));
                    }
                    if("name".equals(item.getFieldName())){
                        name = item.getString(utf8);
                    }
                }
            }
            for(FileItem item : items){
                if(!item.isFormField()){
                    String temFileName = name;
                    if(name != null){
                        if(schunk != null){
                            temFileName = schunk +"_"+name;
                        }
                        File temFile = new File(uploadPath,temFileName);
                        if(!temFile.exists()){//断点续传
                            item.write(temFile);
                        }
                    }
                }
            }
            //文件合并
            if(schunk != null && schunk.intValue() == schunks.intValue()-1){
                File tempFile = new File(uploadPath,name);
                os = new BufferedOutputStream(new FileOutputStream(tempFile));

                for(int i=0 ;i<schunks;i++){
                    File file = new File(uploadPath,i+"_"+name);
                    while(!file.exists()){
                        Thread.sleep(100);
                    }
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    os.write(bytes);
                    os.flush();
                    file.delete();
                }
                os.flush();

                //将文件信息保存到附件表当中
                 attached = new Attached();
                attached.setAttached_addr(uploadPath);
                attached.setAttched_name(FileNameUtils.getFileName(name));
                //修改文件名称

                //xianyi

                attached.setDoctor_id(((Doctor_info)session.getAttribute("doctor_info")).getId());
                attached.setCreate_time(Timestamp.valueOf(LocalDateTime.now()));
                attachedService.save(attached);

                result.setAttachedId(attached.getId()+"");
            }
            return result;
//            response.getWriter().write("上传成功"+name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(os != null){
                    os.close();
                    if(attached!=null){
                        File file = new File(uploadPath+""+name);
                        File newFile = new File(uploadPath+""+attached.getAttched_name());
                        if(file.renameTo(newFile)){
                            System.out.println("ok");
                        }else{
                            System.out.println("delete");
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     *
     */
    @RequestMapping("/file_result_upload")
    @ResponseBody
    public Result fileUploadResult(HttpServletResponse response, HttpServletRequest request, HttpSession session){
        Result result = new Result();
        //分片
        Attached_result attached = null;
        response.setCharacterEncoding(utf8);
        Integer schunk = null;
        Integer schunks = null;
        String name = null;
        BufferedOutputStream os = null;
        String uploadPath = up;
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            factory.setRepository(new File(uploadPath));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(5L * 1024L * 1024L * 1024L);
            upload.setSizeMax(10L * 1024L * 1024L * 1024L);
            List<FileItem> items = upload.parseRequest(request);

            for(FileItem item : items){
                if(item.isFormField()){
                    if("chunk".equals(item.getFieldName())){
                        schunk = Integer.parseInt(item.getString(utf8));
                    }
                    if("chunks".equals(item.getFieldName())){
                        schunks = Integer.parseInt(item.getString(utf8));
                    }
                    if("name".equals(item.getFieldName())){
                        name = item.getString(utf8);
                    }
                }
            }
            for(FileItem item : items){
                if(!item.isFormField()){
                    String temFileName = name;
                    if(name != null){
                        if(schunk != null){
                            temFileName = schunk +"_"+name;
                        }
                        File temFile = new File(uploadPath,temFileName);
                        if(!temFile.exists()){//断点续传
                            item.write(temFile);
                        }
                    }
                }
            }
            //文件合并
            if(schunk != null && schunk.intValue() == schunks.intValue()-1){
                File tempFile = new File(uploadPath,name);
                os = new BufferedOutputStream(new FileOutputStream(tempFile));

                for(int i=0 ;i<schunks;i++){
                    File file = new File(uploadPath,i+"_"+name);
                    while(!file.exists()){
                        Thread.sleep(100);
                    }
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    os.write(bytes);
                    os.flush();
                    file.delete();
                }
                os.flush();

                //上传成功，保存文件信息
                 attached = new Attached_result();
                attached.setAttached_addr(uploadPath);
                attached.setAttched_name(FileNameUtils.getFileName(name));
                attached.setCreate_time(Timestamp.valueOf(LocalDateTime.now()));
                attached.setDoctor_id(  ((Doctor_info) session.getAttribute("doctor_info")).getId());
                attachedResultService.save(attached);
                //返回id
                result.setAttachedId(attached.getId()+"");

            }
            return result;
//            response.getWriter().write("上传成功"+name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(os != null){
                    os.close();
                    if(attached!=null){
                        File file = new File(uploadPath+""+name);
                        File newFile = new File(uploadPath+""+attached.getAttched_name());
                        if(file.renameTo(newFile)){
                            System.out.println("ok");
                        }else{
                            System.out.println("delete");
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }
}
