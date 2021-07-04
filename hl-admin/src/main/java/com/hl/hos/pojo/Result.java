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
}
