package com.hl.hos.utils;

import java.util.UUID;

/**
 * 文件名生成工具
 */
public class FileNameUtils {
    public static String getFileName(String fileName){
        String uuid = UUID.randomUUID().toString().replace("-","");
        return uuid+"_"+fileName;
    }
}
