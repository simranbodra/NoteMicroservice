package com.bridgelabz.microserice.fundoonotes.note.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.FileConversionException;

@Service
public class S3ImageStorage implements ImageStorageService {

	private static final String SUFFIX = "/";

	@Value("${s3AccessKey}")
	String accessKeyID;

	@Value("${s3SecretAccessKey}")
	String secretAccessKey;

	@Value("${bucketName}")
	String bucketName;

	public AmazonS3 getS3Client() {
		AWSCredentials awsCredentials = null;
		try {
			awsCredentials = new AWSCredentials() {
				@Override
				public String getAWSSecretKey() {
					return secretAccessKey;
				}

				@Override
				public String getAWSAccessKeyId() {
					return accessKeyID;
				}
			};
		} catch (Exception exception) {
			throw new AmazonClientException("can not load your aws credentials, please check your credentials !!",
					exception);
		}
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion("us-east-1").build();

		return s3Client;
	}

	@Override
	public void createBucket(String bucketName) {
		AmazonS3 s3client = getS3Client();

		s3client.createBucket(bucketName);
	}

	@Override
	public List<String> getBuckets() {
		AmazonS3 s3client = getS3Client();

		List<String> bucketList = new ArrayList<>();
		for (Bucket bucket : s3client.listBuckets()) {
			bucketList.add(bucket.getName());
		}

		return bucketList;
	}

	@Override
	public void deleteBucket(String bucketName) {
		AmazonS3 s3client = getS3Client();

		s3client.deleteBucket(bucketName);
	}

	@Override
	public void createFolder(String folderName) {
		AmazonS3 s3client = getS3Client();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyContent,
				metadata);

		s3client.putObject(putObjectRequest);
	}

	@Override
	public void deleteFolder(String folderName) {
		AmazonS3 s3client = getS3Client();

		List<S3ObjectSummary> fileList = s3client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			s3client.deleteObject(bucketName, file.getKey());
		}
		s3client.deleteObject(bucketName, folderName);
	}

	@Override
	public void uploadFile(String folderName, MultipartFile fileLocation) throws FileConversionException {
		AmazonS3 s3client = getS3Client();

		File file = multiPartFileToFile(fileLocation);

		String fileName = folderName + SUFFIX + fileLocation.getOriginalFilename();

		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));

	}

	@Override
	public void deleteFile(String folderName, String deleteFileName) {
		AmazonS3 s3client = getS3Client();

		List<S3ObjectSummary> fileList = s3client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			if (deleteFileName.equals(file.getKey())) {
				s3client.deleteObject(bucketName, file.getKey());
			}
		}
	}

	@Override
	public String getFile(String folderName, String fileName) {
		AmazonS3 s3client = getS3Client();

		String folder = folderName + SUFFIX;

		List<S3ObjectSummary> fileList = s3client.listObjects(bucketName, folder).getObjectSummaries();

		String url = null;
		for (S3ObjectSummary file : fileList) {
			if ((folder + fileName).equals(file.getKey())) {
				url = ((AmazonS3Client) s3client).getResourceUrl(bucketName, file.getKey());
			}
		}
		return url;
	}

	@Override
	public List<String> getFiles(String folderName) {
		AmazonS3 s3client = getS3Client();

		List<S3ObjectSummary> fileList = s3client.listObjects(bucketName, folderName).getObjectSummaries();

		List<String> filesUrl = new ArrayList<>();
		for (S3ObjectSummary file : fileList) {

			String url = ((AmazonS3Client) s3client).getResourceUrl(bucketName, file.getKey());

			filesUrl.add(url);
		}

		return filesUrl;
	}

	public File multiPartFileToFile(MultipartFile file) throws FileConversionException {
		File convFile = new File(file.getOriginalFilename());
		boolean value;
		try {
			value = convFile.createNewFile();
		} catch (IOException exception) {
			throw new FileConversionException("Error while multi part file conversion");
		}
		if (value) {
			try (FileOutputStream fos = new FileOutputStream(convFile)) {
				fos.write(file.getBytes());
			} catch (IOException exception) {
				throw new FileConversionException("Error while multi part file conversion");
			}
		}

		return convFile;
	}

}
