package com.itheima;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import com.aliyun.sdk.service.oss2.transport.BinaryData;

import java.io.File;
import java.nio.file.Files;

public class Demo {

    public static void main(String[] args) {

        String endpoint =
                "https://oss-cn-shanghai.aliyuncs.com";

        String region = "cn-shanghai";

        String bucketName = "java-web-buwlc";

        String objectName = "001.jpg";

        String filePath =
                "E:\\CodeJava\\web-ai-project02\\images\\0f7b82fb7ac54dcabf1c15f8b2be310a.jpg";

        CredentialsProvider provider =
                new EnvironmentVariableCredentialsProvider();

        OSSClientBuilder clientBuilder =
                OSSClient.newBuilder()
                        .credentialsProvider(provider)
                        .region(region)
                        .endpoint(endpoint);

        try (OSSClient client = clientBuilder.build()) {

            File file = new File(filePath);

            // 读取文件为 byte[]
            byte[] content =
                    Files.readAllBytes(file.toPath());

            // 构造上传请求
            PutObjectRequest request =
                    PutObjectRequest.newBuilder()
                            .bucket(bucketName)
                            .key(objectName)
                            .body(BinaryData.fromBytes(content))
                            .build();

            // 上传
            PutObjectResult result =
                    client.putObject(request);

            System.out.println("上传成功");
            System.out.println(
                    "statusCode = " + result.statusCode()
            );
            System.out.println(
                    "requestId = " + result.requestId()
            );
            System.out.println(
                    "eTag = " + result.eTag()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}