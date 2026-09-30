package com.itheima.controller;

import com.itheima.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {

    private static final String UPLOAD_DIR = "E:\\CodeJava\\web-ai-project02\\images\\";
    // 上传文件 - 参数名 file
    @PostMapping("/upload")
    public Result upload(@RequestParam("name") String username, Integer age, @RequestParam("file") MultipartFile iamge) throws IOException {
        log.info("上传文件：{},{},{}",username,age,iamge);
        if(!iamge.isEmpty()){
            // 生成唯一文件名
            String originalFilename = iamge.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString().replace("-","") + extName;

            //拼接完整的文件路径
            File targetFile = new File(UPLOAD_DIR + uniqueFileName);

            // 如果目标目录不存在，则创建它
            if(!targetFile.getParentFile().exists()){
                targetFile.getParentFile().mkdirs();
            }

            // 保存文件
            iamge.transferTo(targetFile);
        }

        return Result.success();
    }
}
