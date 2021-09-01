package com.hl.hos.backPogo;

import com.hl.hos.pojo.Doctor_info;
import com.hl.hos.pojo.Hos_info;
import com.hl.hos.pojo.Template;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 医生和标准附件
 */
@Data
@Component
public class DoctorFile implements Serializable
{
    private Doctor_info doctor_info;
    private Template template;
}
