package com.hl.hos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DoctorHos;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.mapper.Doctor_infoMapper;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.service.Doctor_infoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hl.hos.service.Hos_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-02
 */
@Service
public class Doctor_infoServiceImpl extends ServiceImpl<Doctor_infoMapper, Doctor_info> implements Doctor_infoService {
    @Autowired
    private Doctor_infoService doctor_infoService;
    @Autowired
    private Hos_infoService hos_infoService;

    @Override
    public List<DoctorHos> get_hos_doc_page(QueryWrapper<Doctor_info> queryWrapper, String page, String limit)
    {
        List<DoctorHos> doctorLists = new ArrayList<>();
        Page<Doctor_info> page1 = new Page<>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Doctor_info> iPage = doctor_infoService.page(page1,queryWrapper);

        for (int i = 0; i < iPage.getRecords().size(); i++)
        {
            DoctorHos doctorHos = new DoctorHos();
            Doctor_info doctorInfo = iPage.getRecords().get(i);
            doctorInfo.setDoctor_pwd(null);
            doctorHos.setDoctor_info(doctorInfo);
            doctorHos.setHos_info(hos_infoService.getById(doctorInfo.getHos_id()));
            doctorLists.add(doctorHos);
        }
        return doctorLists;
    }
}
