package com.hl.hos.controller;

import com.hl.hos.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file")MultipartFile[] multipartFiles){
        Result result = new Result();

        if(multipartFiles.length == 0 ){

        }
        return result;
    }
}
