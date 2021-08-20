package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.pojo.Attached;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.AttachedService;
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
 * @author 何夜息
 * @since 2021-07-02
 */
@RestController
public class AttachedController {

    @Autowired
    private AttachedService attachedService;
    @GetMapping("/deleteThisFile")
    public Result deleteThisFile(String fileName){
        Result result = new Result();
        Attached attachedByName = attachedService.getOne(new QueryWrapper<Attached>().eq("attched_name", fileName));
        if(attachedByName == null){
            result.setMsg("error");
        }else {
            File file = new File(attachedByName.getAttached_addr()+attachedByName.getAttched_name());
            if(file.delete()){
                //如果删除成功
                //再讲数据删除记录
                boolean b = attachedByName.deleteById();
                if(b)
                    result.setMsg("success");
                else
                    result.setMsg("error");
            }else{
                //删除失败
                result.setMsg("error");
            }
        }
        return result;
    }
}

