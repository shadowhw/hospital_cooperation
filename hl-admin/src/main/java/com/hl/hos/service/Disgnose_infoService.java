package com.hl.hos.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hl.hos.backPogo.DiagnosisDoctorHos;
import com.hl.hos.pojo.Disgnose_info;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-02
 */
public interface Disgnose_infoService extends IService<Disgnose_info> {

    /**
     * 分页查询诊断数据
     * @param queryWrapper
     * @param page
     * @param limit
     * @return
     */
    List<DiagnosisDoctorHos> get_dia_by_page(QueryWrapper<Disgnose_info> queryWrapper,String page,String limit);

}
