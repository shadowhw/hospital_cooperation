package com.hl.hos.backPogo;

import com.hl.hos.pojo.Attached;
import com.hl.hos.pojo.Attached_result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AttachedWithResutAttached {
    private String type ; //1 诊断申请附件 //2为诊断结果附件
    private Attached attached;
}
