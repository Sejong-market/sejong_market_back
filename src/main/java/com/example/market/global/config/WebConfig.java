package com.example.market.global.config;

import com.example.market.global.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 업로드된 이미지를 클라이언트가 정적 리소스 URL 로 조회할 수 있도록 매핑한다.
 *
 * 예) GET /uploads/products/abc.jpg  →  ./uploads/products/abc.jpg
 *
 * CORS 설정은 SecurityConfig 에서 별도로 처리하므로 여기서는 다루지 않는다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final FileStorageService fileStorageService;

	@Value("${app.upload.url-prefix:/uploads}")
	private String urlPrefix;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = "file:" + fileStorageService.getProductImageDir().getParent().toString() + "/";
		registry.addResourceHandler(urlPrefix + "/**")
				.addResourceLocations(location);
	}
}
