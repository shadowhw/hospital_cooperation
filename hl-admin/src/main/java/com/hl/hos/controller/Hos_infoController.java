package com.hl.hos.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Result;
import com.hl.hos.service.Hos_infoService;
import com.hl.hos.utils.DateUtil;
import com.mysql.cj.conf.HostInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
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
        QueryWrapper<Hos_info> queryWrapper = new QueryWrapper<Hos_info>().eq("stat",1);

        List<Hos_info> list = hos_infoService.list(queryWrapper);

        Page<Hos_info> page1 = new Page<Hos_info>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Hos_info> iPage = hos_infoService.page(page1,queryWrapper);

        result.setCount(list.size());//数量应该是所有数据的大小
        result.setData(iPage.getRecords());
        result.setCode(200);
        return result;
    }

    /**
     * 不分页查询所有医院信息：只查询通过状态为1的医院
     * @return
     */
    @ResponseBody
    @GetMapping("/get_hos_list_no_page")
    public Result get_hos_list_no_page()
    {
        List<Hos_info> hos_list = hos_infoService.list(new QueryWrapper<Hos_info>()
                .select("id","hos_name")
                .eq("stat",1)
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
            result.setData(null);
        }else {
            result.setMsg("该医院已经存在！");
            result.setCount(0);
            result.setCode(201);
        }
        return result;
    }

    /**
     * 批量导入医院信息
     * @return
     */
    @ResponseBody
    @PostMapping("/batch_add_hos")
    public Result batch_add_hos(@RequestBody String hos_infos)
    {
        String decode = URLDecoder.decode(hos_infos);
        JSONArray jsonArray = JSONArray.parseArray(decode.substring(10));
        List<Hos_info> list = jsonArray.toJavaList(Hos_info.class);
        for (int i = 0; i < list.size(); i++)
        {
            //存在后不能添加
            Hos_info tem = hos_infoService.getOne(new QueryWrapper<Hos_info>()
                    .eq("hos_name",list.get(i).getHos_name())
                    .eq("hos_addr",list.get(i).getHos_addr())
            );
            //
            if(tem==null)
            {
                list.get(i).setCreate_time(DateUtil.getNowSqlDateTime());
                list.get(i).setStat(1);
                list.get(i).setComment_text("批量导入医院信息");
                hos_infoService.save(list.get(i));
            }else {
                //System.out.println("存在");
            }
        }
        result.setMsg("添加成功");
        result.setData(null);
        result.setCode(200);
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

    /**
     * 组合查询
     */
    @ResponseBody
    @GetMapping("/get_hos_by_argments")
    public Result get_hos_by_argments(String hos_name,String hos_addr){
        Result result = new Result();
        QueryWrapper<Hos_info> queryWrapper = new QueryWrapper<Hos_info>();
        if(!StringUtils.isEmpty(hos_addr.trim())){ //
            queryWrapper.like("hos_addr",hos_addr);
        }

        if(!StringUtils.isEmpty(hos_name.trim())){
            queryWrapper.like("hos_name",hos_name);
        }

         List<Hos_info> hos_infoList= hos_infoService.list(queryWrapper);
        result.setData(hos_infoList);
        result.setCode(201);
        result.setCount(hos_infoList.size());
        result.setMsg("");

        return result;
    }
}

