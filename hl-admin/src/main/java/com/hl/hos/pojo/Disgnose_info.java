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

    private String patient_STS; //评分

    private String patient_LVDd; //左室舒张末期内径

    private String patient_LVDs; //左室收缩末期内径

    private String patient_LVEF; //左室射血分数；

    private String patient_VD; //瓣口直径；

    private String patient_VA; //瓣口面积

    private String patient_AVPPG;//主动脉瓣峰值压力阶差

    private String patient_AVAPG; //主动脉瓣平均压力阶差

    private String patient_AS; //主动脉狭窄

    private String patient_AVI ; //主动脉瓣关闭不全

    private String patient_MS; //二尖瓣狭窄

    private String patient_MI; //二尖瓣关闭不全
}
