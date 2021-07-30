package com.hl.hos.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.sql.Timestamp;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("t_attached")
public class Attached implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long disgnose_id;

    private Long doctor_id;

    private String attached_addr;

    private String attched_name;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp create_time;

    private Integer stat;

    private String comment_text;


}
