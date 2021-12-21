package com.hl.hos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.hos.backPogo.DiagnosisDoctorHos;
import com.hl.hos.pojo.*;
import com.hl.hos.mapper.Disgnose_infoMapper;
import com.hl.hos.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class Disgnose_infoServiceImpl extends ServiceImpl<Disgnose_infoMapper, Disgnose_info> implements Disgnose_infoService {
    @Autowired
    private Disgnose_infoService disgnose_infoService;
    @Autowired
    private Doctor_infoService doctor_infoService;
    @Autowired
    private Hos_infoService hos_infoService;
    @Autowired
    private Doctor_with_disgnoseService doctor_with_disgnoseService;
    @Autowired
    private AttachedService attachedService;
    @Autowired
    private Attached_resultService attached_resultService;

    @Override
    public List<DiagnosisDoctorHos> get_dia_by_page(QueryWrapper<Disgnose_info> queryWrapper, String page, String limit)
    {
        List<DiagnosisDoctorHos> resList = new ArrayList<>();
        Page<Disgnose_info> page1 = new Page<>(Integer.parseInt(page),Integer.parseInt(limit));
        IPage<Disgnose_info> iPage = disgnose_infoService.page(page1,queryWrapper);

        for (int i = 0; i < iPage.getRecords().size(); i++)
        {
            DiagnosisDoctorHos diagnosisDoctorHos = new DiagnosisDoctorHos();
            Disgnose_info disgnose_info = iPage.getRecords().get(i);

            //查询协作医师姓名
            List<Doctor_with_disgnose> doctor_with_disgnose = doctor_with_disgnoseService.list(new QueryWrapper<Doctor_with_disgnose>()
                    .eq("disgnose_id",disgnose_info.getId())
            );
            if(doctor_with_disgnose.size()>=1)
            {
                Doctor_info assist = doctor_infoService.getById(doctor_with_disgnose.get(0).getDoctor_id());
                //当前负责人
                Hos_info hosInfo = hos_infoService.getById(assist.getHos_id());
                diagnosisDoctorHos.setAssist_doctor_name(hosInfo.getHos_name()+"-"+assist.getDoctor_name());
            }else {
                diagnosisDoctorHos.setAssist_doctor_name("暂无");
            }

            //医生账号不能用时不显示
            Doctor_info doctorInfo = doctor_infoService.getById(disgnose_info.getDoctor_id());
            //医生账号被删除
            if(doctorInfo==null)
                continue;
            if(doctorInfo.getStat()==3 || doctorInfo.getStat()==4)
                continue;//不绑定信息
            doctorInfo.setDoctor_pwd(null);
            diagnosisDoctorHos.setDoctor_info(doctorInfo);//绑定医生
            Long hos_id = doctor_infoService.getById(disgnose_info.getDoctor_id()).getHos_id();
            diagnosisDoctorHos.setHos_info(hos_infoService.getById(hos_id));//绑定医院信息
            diagnosisDoctorHos.setDisgnose_info(disgnose_info);//绑定诊断信息
            resList.add(diagnosisDoctorHos);
        }
        return resList;
    }

    /**
     * 删除诊断信息
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)//开启事务
    public int delDisnoseById(Long id)
    {
        //删除诊断表
        disgnose_infoService.removeById(id);
        //删除附件表
        attachedService.remove(new QueryWrapper<Attached>()
            .eq("disgnose_id",id)
        );
        //删除协作表
        doctor_with_disgnoseService.remove(new QueryWrapper<Doctor_with_disgnose>()
            .eq("disgnose_id",id)
        );

        return 1;
    }
}
