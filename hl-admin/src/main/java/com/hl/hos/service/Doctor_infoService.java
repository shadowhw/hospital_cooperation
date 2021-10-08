package com.hl.hos.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.backPogo.DoctorHos;
import com.hl.hos.pojo.Doctor_info;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hl.hos.pojo.Hos_info;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-02
 */
public interface Doctor_infoService extends IService<Doctor_info> {

    /**
     * 分页查询医生信息并且绑定医院
     * @param queryWrapper
     * @param page
     * @param limit
     * @return
     */
    List<DoctorHos> get_hos_doc_page(QueryWrapper<Doctor_info> queryWrapper,String page,String limit);
}
