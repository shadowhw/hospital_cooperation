package com.hl.hos.backPogo;

import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 医生和医院关联信息实体
 */
@Data
@Component
public class DoctorHos implements Serializable
{
    private Doctor_info doctor_info;
    private Hos_info hos_info;
}
