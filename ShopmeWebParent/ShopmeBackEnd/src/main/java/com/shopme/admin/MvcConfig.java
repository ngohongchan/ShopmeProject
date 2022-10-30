package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {	
		String dirname = "user-photos";
		
		Path userPhotosDir = Paths.get(dirname);
		
		String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/" + dirname + "/**").addResourceLocations("file:/" + userPhotosPath + "/");


		String categoryImageName = "../category-images";

		Path categoryImageDir = Paths.get(categoryImageName);

		String categoryImagePath = categoryImageDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagePath + "/");
	}

}
