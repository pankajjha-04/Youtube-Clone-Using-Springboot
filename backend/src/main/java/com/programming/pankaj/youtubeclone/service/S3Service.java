package com.programming.pankaj.youtubeclone.service;  // Package declaration for the service class

import com.amazonaws.services.s3.AmazonS3Client;  // Importing the Amazon S3 client class
import com.amazonaws.services.s3.model.CannedAccessControlList;  // Importing the CannedAccessControlList class from S3
import com.amazonaws.services.s3.model.ObjectMetadata;  // Importing the ObjectMetadata class from S3
import lombok.RequiredArgsConstructor;  // Importing Lombok annotation for constructor injection
import org.springframework.http.HttpStatus;  // Importing HttpStatus class from Spring framework
import org.springframework.stereotype.Service;  // Importing Service annotation from Spring framework
import org.springframework.util.StringUtils;  // Importing StringUtils class from Spring framework
import org.springframework.web.multipart.MultipartFile;  // Importing MultipartFile class from Spring framework
import org.springframework.web.server.ResponseStatusException;  // Importing ResponseStatusException class from Spring framework

import java.io.IOException;  // Importing IOException class from Java IO
import java.util.UUID;  // Importing UUID class from Java Util

@Service  // Indicates that this class is a service component
@RequiredArgsConstructor  // Lombok annotation to generate constructor with required arguments
public class S3Service implements FileService {  // Class declaration, implementing FileService interface

    public static final String BUCKET_NAME = "pankajspring";  // Static variable for the bucket name
    private final AmazonS3Client awsS3Client;  // Instance variable for the Amazon S3 client

    // Method to upload a file to Amazon S3
    @Override
    public String uploadFile(MultipartFile file) {
        // Get the file extension
        var filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        // Generate a unique key for the file using a UUID and the file extension
        var key = UUID.randomUUID().toString() + "." + filenameExtension;

        // Create metadata for the file
        var metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            // Upload the file to the specified bucket in Amazon S3
            awsS3Client.putObject(BUCKET_NAME, key, file.getInputStream(), metadata);
        } catch (IOException ioException) {
            // If an exception occurs during the file upload, throw a response status exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An Exception occurred while uploading the file");
        }

        // Set the access control list for the uploaded file to public read
        awsS3Client.setObjectAcl(BUCKET_NAME, key, CannedAccessControlList.PublicRead);

        // Return the URL of the uploaded file
        return awsS3Client.getResourceUrl(BUCKET_NAME, key);
    }
}
//ALT + CONTROL + C (convert pankajspring to BUCKET_NAME)