package com.hl.hos.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author 何夜息
 * @since 2021-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_disgnose_info")
public class Disgnose_info extends Model<Disgnose_info> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long doctor_id;

    private String patient_name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Timestamp patient_birth;

    private String patient_tall;

    private String patient_weight;

    private String diagnose_result;

    private String department;

    private String patient_BMI;

    private Timestamp create_time;

    private Integer stat;

    private String comment_text;

    private String disgnose_code;//诊断编号：年月日+三位数从1开始编
}
