package com.hl.hos.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 返回前端数据
 */
@Data
@Component
public class Result
{
    private String msg;
    private Integer code;
    private Object data;
    private Integer count;
    private String userName;//当前用户名称
    private String attachedId;//文件的id
}
