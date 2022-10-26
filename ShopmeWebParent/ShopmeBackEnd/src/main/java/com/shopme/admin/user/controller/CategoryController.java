package com.shopme.admin.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.user.CategoryService;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	private String listAll(Model model) {
		List<Category> listCategories = categoryService.listsAll();
		model.addAttribute("listCategories", listCategories);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	private String newCategory() {
		return "categories/categories_form";
	}
	
}
