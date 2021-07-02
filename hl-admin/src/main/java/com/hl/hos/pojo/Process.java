package com.hl.hos.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.sql.Timestamp;

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
@TableName("t_process")
public class Process implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long disgnose_id;

    private String fenfa;

    private String transactor;

    private Timestamp fenfa_time;

    private String fenfa_comment;

    private String transact_type;

    private Timestamp transact_time;

    private Integer transact_stat;

    private String transact_comment;

    private Timestamp create_time;

    private Integer stat;

    private String comment_text;


}
