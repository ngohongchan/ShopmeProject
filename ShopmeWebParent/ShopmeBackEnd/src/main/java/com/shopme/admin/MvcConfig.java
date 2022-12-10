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
		exposeDirectory("user-photos", registry);
		exposeDirectory("../category-images", registry);
		exposeDirectory("../brand-logos", registry);


//		String dirname = "user-photos";
//
//		Path userPhotosDir = Paths.get(dirname);
//
//		String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
//
//		registry.addResourceHandler("/" + dirname + "/**").addResourceLocations("file:/" + userPhotosPath + "/");


//		String categoryImageName = "../category-images";
//
//		Path categoryImageDir = Paths.get(categoryImageName);
//
//		String categoryImagePath = categoryImageDir.toFile().getAbsolutePath();
//
//		registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagePath + "/");

//		String brandImageName = "../brand-logos";
//
//		Path brandImageDir = Paths.get(brandImageName);
//
//		String brandImagePath = brandImageDir.toFile().getAbsolutePath();
//
//		registry.addResourceHandler("/brand-logos/**").addResourceLocations("file:/" + brandImagePath + "/");

	}

	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicalPath = pathPattern.replace("..", "") + "/**";

		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");
	}

}
