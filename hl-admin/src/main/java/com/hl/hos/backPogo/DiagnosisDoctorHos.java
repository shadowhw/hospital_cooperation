package com.hl.hos.backPogo;

import com.hl.hos.pojo.Disgnose_info;
import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 诊断信息、医生和医院关联信息实体
 */
@Data
@Component
public class DiagnosisDoctorHos implements Serializable
{
    private Doctor_info doctor_info;
    private Hos_info hos_info;
    private Disgnose_info disgnose_info;
}
