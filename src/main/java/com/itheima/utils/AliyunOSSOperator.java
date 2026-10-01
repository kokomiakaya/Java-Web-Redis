package com.itheima.utils;


import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class AliyunOSSOperator {

    // OSS访问域名
    private String endpoint = "https://oss-cn-shanghai.aliyuncs.com";

    // Bucket名称
    private String bucketName = "java-web-buwlc";

    // Bucket所在区域
    private String region = "cn-shanghai";

    /**
     * 上传文件
     *
     * @param content          文件字节数据
     * @param originalFilename 原始文件名
     * @return 文件访问地址
     */
    public String upload(byte[] content, String originalFilename) throws Exception {

        // 1. 获取当前日期，作为OSS中的目录，例如：2026/10
        String dir = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 2. 获取原文件扩展名，例如 .jpg
        String suffix =
                originalFilename.substring(originalFilename.lastIndexOf("."));

        // 3. 使用UUID生成唯一文件名
        String newFileName =
                UUID.randomUUID() + suffix;

        // 最终OSS中的Object名称
        // 例如：2026/10/xxxx-xxxx-xxxx.jpg
        String objectName =
                dir + "/" + newFileName;

        // 4. 创建V2版本OSS客户端
        try (OSSClient client = OSSClient.newBuilder()
                .credentialsProvider(
                        new EnvironmentVariableCredentialsProvider()
                )
                .region(region)
                .endpoint(endpoint)
                .build()) {

            // 5. 构造上传请求
            PutObjectRequest request =
                    PutObjectRequest.newBuilder()
                            .bucket(bucketName)
                            .key(objectName)
                            .body(BinaryData.fromBytes(content))
                            .build();

            // 6. 执行上传
            client.putObject(request);
        }

        // 7. 返回文件访问地址
        return "https://"
                + bucketName
                + ".oss-"
                + region
                + ".aliyuncs.com/"
                + objectName;
    }
}