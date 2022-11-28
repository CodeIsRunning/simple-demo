package com.xfliu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadControlller {

    @RequestMapping("upload")
    public void fileUpload(MultipartFile file){
        System.out.println(file.getOriginalFilename());
    }

}
