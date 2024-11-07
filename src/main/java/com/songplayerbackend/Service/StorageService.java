package com.songplayerbackend.Service;
import io.github.cdimascio.dotenv.Dotenv;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final AmazonS3 s3Client;
    Dotenv dotenv = Dotenv.load();
    public StorageService() {

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(dotenv.get("AWS_ACCESS_KEY_ID"), dotenv.get("AWS_SECRET_ACCESS_KEY"))
        );

        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion("ap-southeast-2") // specify your AWS region
                .build();

    }

    public List<String> getSongFileNames() {

        ListObjectsV2Result result = s3Client.listObjectsV2("songplayerstorage");
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        return objects.stream()
                .map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    public void uploadSong(MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        s3Client.putObject(new PutObjectRequest("songplayerstorage", file.getOriginalFilename(), file.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
    }

}