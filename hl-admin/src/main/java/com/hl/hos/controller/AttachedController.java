package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.pojo.Attached;
import com.hl.hos.pojo.Attached_result;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.AttachedService;
import com.hl.hos.service.Attached_resultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JianYi
 * @since 2021-07-02
 */
@RestController
public class AttachedController {

    @Autowired
    private AttachedService attachedService;

    @Autowired
    private Attached_resultService attachedResultService;

    @GetMapping("/deleteThisFile")
    public Result deleteThisFile(String fileName){
        Result result = new Result();
        Attached attached = attachedService.getOne(new QueryWrapper<Attached>().eq("attched_name", fileName));
        Attached_result attachedResult = attachedResultService.getOne(new QueryWrapper<Attached_result>().eq("attched_name", fileName));
        if(attached == null && attachedResult == null){
            result.setMsg("error");
        }else {
            File file ;
            if(attached != null){ //删除诊断申请附件
                 file = new File(attached.getAttached_addr()+attached.getAttched_name());
                if(file.delete()){
                    boolean b = attached.deleteById();
                    if(b)
                        result.setMsg("success");
                    else
                        result.setMsg("error");
                }else
                    //删除失败
                    result.setMsg("error");

            }else{ //删除诊断结果附件
                file = new File(attachedResult.getAttached_addr()+attachedResult.getAttched_name());
                if(file.delete()){
                    boolean b = attachedResult.deleteById();
                    if(b)
                        result.setMsg("success");
                    else
                        result.setMsg("error");
                }else
                    //删除失败
                    result.setMsg("error");
            }
        }
        return result;
    }
}

