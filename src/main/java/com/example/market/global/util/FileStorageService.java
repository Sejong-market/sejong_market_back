package com.example.market.global.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 업로드된 이미지를 로컬 파일 시스템에 저장하고,
 * 클라이언트가 접근 가능한 URL 경로(/uploads/products/...) 를 반환한다.
 *
 * 저장 디렉토리/URL prefix 는 application.yml 의 app.upload.* 값으로 주입된다.
 */
@Slf4j
@Component
public class FileStorageService {

	/** 허용 확장자. 보안/안정성을 위해 명시적으로 화이트리스트 처리한다. */
	private static final Set<String> ALLOWED_EXTENSIONS =
			Set.of("jpg", "jpeg", "png", "gif", "webp");

	private final String uploadDir;
	private final String urlPrefix;

	private Path productImageDir;

	public FileStorageService(
			@Value("${app.upload.dir:./uploads}") String uploadDir,
			@Value("${app.upload.url-prefix:/uploads}") String urlPrefix) {
		this.uploadDir = uploadDir;
		this.urlPrefix = urlPrefix;
	}

	@PostConstruct
	public void init() {
		try {
			this.productImageDir = Paths.get(uploadDir, "products").toAbsolutePath().normalize();
			Files.createDirectories(productImageDir);
			log.info("상품 이미지 저장 디렉토리 준비 완료: {}", productImageDir);
		} catch (IOException e) {
			throw new IllegalStateException("이미지 저장 디렉토리를 생성할 수 없습니다: " + uploadDir, e);
		}
	}

	/**
	 * 상품 이미지를 저장하고, DB 에 저장할 URL 을 반환한다.
	 *
	 * @param image 업로드된 이미지 파일. null 또는 비어있으면 null 반환.
	 * @return 저장된 이미지의 접근 URL(예: /uploads/products/{uuid}.jpg). 입력이 비어있으면 null.
	 */
	public String storeProductImage(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return null;
		}

		String originalName = StringUtils.cleanPath(
				image.getOriginalFilename() == null ? "" : image.getOriginalFilename());
		String extension = extractExtension(originalName);

		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new IllegalArgumentException(
					"지원하지 않는 이미지 형식입니다. (허용: " + ALLOWED_EXTENSIONS + ")");
		}

		String savedFileName = UUID.randomUUID() + "." + extension;
		Path targetPath = productImageDir.resolve(savedFileName);

		try {
			Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
		}

		return urlPrefix + "/products/" + savedFileName;
	}

	/**
	 * 정적 리소스 핸들러에서 사용할 절대 경로(file:...) 를 반환한다.
	 */
	public Path getProductImageDir() {
		return productImageDir;
	}

	private String extractExtension(String filename) {
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == filename.length() - 1) {
			return "";
		}
		return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
	}
}
