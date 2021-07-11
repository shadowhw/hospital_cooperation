package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.pojo.Process;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/hos/process")
public class ProcessController {

    @Autowired
    private Result result;
    @Autowired
    ProcessService processService;

    /**
     * 根据诊断编号查询诊断流程
     * @return
     */
    @ResponseBody
    @GetMapping("/get_process_data_by_id")
    public Result get_process_data_by_id(Long disgnose_id)
    {
        List<Process> list = processService.list(new QueryWrapper<Process>()
                .eq("disgnose_id",disgnose_id)
        );

        result.setCount(list.size());
        result.setData(list);
        result.setCode(200);
        return result;
    }
}

