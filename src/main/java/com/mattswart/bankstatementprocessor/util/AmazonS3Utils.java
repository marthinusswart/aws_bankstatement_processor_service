package com.mattswart.bankstatementprocessor.util;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Component
public class AmazonS3Utils {
    private String s3Bbucket = "aws-applications-and-services";
    private String serverDetailsKey = "bankstatement_processor/running_servers.json";
    private String regionString = "ap-southeast-2";
    private Region region = Region.AP_SOUTHEAST_2;

    public AmazonS3Utils() {
    }

    public String loadJsonFile() throws Exception{
        String jsonDataString = "";

        S3Client s3Client = S3Client.builder()
            .region(region)
            .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Bbucket)
                    .key(serverDetailsKey)
                    .build();

        jsonDataString = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asUtf8String();        
        
        return jsonDataString;
    }

}
