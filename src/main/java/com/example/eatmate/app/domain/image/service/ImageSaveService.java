package com.example.eatmate.app.domain.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.eatmate.app.domain.image.domain.Image;
import com.example.eatmate.app.domain.image.domain.repository.ImageRepository;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageSaveService {

	private static final String HTTPS_PROTOCOL = "https://";
	// 지원하는 이미지 파일 확장자 목록
	private static final String[] SUPPORTED_EXTENSIONS = {"jpg", "jpeg", "png"};
	private final ImageRepository imageRepository;
	private final AmazonS3 s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private static String generateUniqueFileName(MultipartFile file, String fileExtension) {
		return UUID.randomUUID() + "." + fileExtension;
		// 고유한 파일 이름을 생성
	}

	private static String getFileExtension(String originalFileName) {
		int lastIndex = originalFileName.lastIndexOf(".");
		return (lastIndex == -1) ? "" : originalFileName.substring(lastIndex + 1);
	}

	public List<Image> uploadImages(List<MultipartFile> imageFiles) {
		// 다중 업로드 && 리스트 ","을 기준으로 하나의 문자열 반환
		if (imageFiles == null || imageFiles.isEmpty())
			return List.of();   // images가 비었다면 빈 리스트 반환

		return imageFiles.parallelStream()
			.map(this::uploadImage)
			.toList();
	}

	public Image uploadImage(MultipartFile imageFile) {
		if (imageFile == null || imageFile.isEmpty())
			throw new CommonException(ErrorCode.WRONG_IMAGE_FORMAT);

		String fileExtension = getFileExtension(imageFile.getOriginalFilename());
		if (!isSupportedFileExtension(fileExtension)) {
			throw new CommonException(ErrorCode.WRONG_IMAGE_FORMAT);
		}
		String fileName = generateUniqueFileName(imageFile, fileExtension);
		try (InputStream inputStream = imageFile.getInputStream()) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(imageFile.getContentType());
			objectMetadata.setContentLength(imageFile.getSize());    //객체(파일)의 메타데이터 설정

			s3Client.putObject(
				new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));    // s3에 파일 업로드

		} catch (IOException e) {
			throw new CommonException(ErrorCode.IMAGE_UPLOAD_FAIL);
		}
		Image image = Image.createImage(extractUrl(fileName));    // 파일명들 추출 후 이미지 생성
		imageRepository.save(image);

		return image;
	}

	private boolean isSupportedFileExtension(String fileExtension) {
		for (String ext : SUPPORTED_EXTENSIONS) {
			if (ext.equalsIgnoreCase(fileExtension)) {
				return true;
			}
		}
		return false;
	}

	private String extractUrl(String fileName) {
		// S3 URL을 한 번만 호출하여 결과를 변수에 저장
		java.net.URL s3Url = s3Client.getUrl(bucket, fileName);

		return HTTPS_PROTOCOL + s3Url.getHost() + s3Url.getFile();
	}

}
