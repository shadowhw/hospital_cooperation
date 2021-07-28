package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Hos_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
@RequestMapping("/hos/hos_info")
public class Hos_infoController {
    @Autowired
    private Hos_infoService hos_infoService;
    @Autowired
    private Result result;

    /**
     * 查询所有医院信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_hos_list")
    public Result get_hos_list(String page, String limit)
    {
        List<Hos_info> list = hos_infoService.list();

        Page<Hos_info> page1 = new Page<Hos_info>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Hos_info> iPage = hos_infoService.page(page1);

        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(iPage.getRecords());
        result.setCode(200);
        return result;
    }

    /**
     * 不分页查询所有医院信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_hos_list_no_page")
    public Result get_hos_list_no_page()
    {
        List<Hos_info> hos_list = hos_infoService.list(new QueryWrapper<Hos_info>()
                .select("id","hos_name")
        );

        result.setCount(hos_list.size());
        result.setData(hos_list);
        result.setCode(200);
        return result;
    }
}

