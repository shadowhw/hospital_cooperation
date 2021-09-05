package com.hl.hos.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 
 * </p>
 *
 * @author 何夜息
 * @since 2021-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_template")
@Component
public class Template extends Model<Template> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long doctor_id;

    private String file_name;

    private String file_addr;

    /**
     * 分类
     */
    private String type;

    private String creat_time;

    /**
     * 状态
     */
    private Integer status;


}
