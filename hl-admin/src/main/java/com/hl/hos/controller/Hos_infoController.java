package com.hl.hos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Hos_infoService;
import com.hl.hos.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 添加医院
     * @return
     */
    @ResponseBody
    @PostMapping("/add_hos")
    public Result add_hos(Hos_info hos_info)
    {
        //存在后不能添加
        Hos_info tem = hos_infoService.getOne(new QueryWrapper<Hos_info>()
            .eq("hos_name",hos_info.getHos_name())
            .eq("hos_addr",hos_info.getHos_addr())
        );
        if(tem==null)
        {
            hos_info.setCreate_time(DateUtil.getNowSqlDateTime());
            hos_info.setStat(1);
            hos_info.setComment_text("管理员添加医院");
            hos_infoService.save(hos_info);
            result.setMsg("添加成功");
            result.setCount(1);
            result.setCode(200);
        }else {
            result.setMsg("该医院已经存在！");
            result.setCount(0);
            result.setCode(201);
        }
        return result;
    }

    /**
     * 更新医院信息
     * @return
     */
    @ResponseBody
    @GetMapping("/update_hos_info")
    public Result update_hos_info(Hos_info hos_info)
    {
        //删除
        if(hos_info.getStat()==8)
        {
            hos_infoService.removeById(hos_info.getId());
            result.setCount(1);
            result.setData(null);
            result.setCode(200);
            result.setMsg("删除!");
            return result;
        }
        if(hos_infoService.updateById(hos_info))
        {
            result.setCount(1);
            result.setData(null);
            result.setCode(200);
            result.setMsg("修改成功!");
        }else {
            result.setCount(0);
            result.setData(null);
            result.setCode(201);
            result.setMsg("修改失败!");
        }
        return result;
    }


    /**
     * 根据医院地址查询医院信息
     * @return
     */
    @ResponseBody
    @GetMapping("/get_hos_by_addr")
    public Result get_hos_by_addr(String hos_addr)
    {
        List<Hos_info> hos_list = hos_infoService.list(new QueryWrapper<Hos_info>()
                .eq("hos_addr",hos_addr)
        );

        result.setCount(hos_list.size());
        result.setData(hos_list);
        result.setCode(200);
        return result;
    }
}

