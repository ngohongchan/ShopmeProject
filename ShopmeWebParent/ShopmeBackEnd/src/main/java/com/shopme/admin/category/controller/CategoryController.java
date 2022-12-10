package com.shopme.admin.category.controller;

import java.io.IOException;
import java.util.List;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.user.exceprion.CategoryNotFoundException;
import com.shopme.admin.user.export.CategoryCsvExporter;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.category.service.CategoryService;
import com.shopme.common.entity.Category;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    private String listFirstPage(@Param("sortDir") String sortDir, Model model) {
        return listByPage(1, sortDir, null, model);

    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortDir") String sortDir, @Param("keyword") String keyword, Model model) {
        if(sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();
        List<Category> listCategories = categoryService.listsByPage(categoryPageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE;
        long endCount =  startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE;
        if(endCount > categoryPageInfo.getTotalElements()) {
            endCount = categoryPageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", reverseSortDir);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    private String newCategory(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes,
                               @RequestParam("fileImage") @NotNull MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        category.setImage(fileName);

        Category savedCategory = categoryService.save(category);
        String uploadDir = "../category-images/" + savedCategory.getId();
        FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        redirectAttributes.addFlashAttribute("message", "The category has been save successfully.");

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable("id") Integer id,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("pageTitle", "Edit Category (Id: " + id + ")");

            Category category = categoryService.get(id);
            model.addAttribute("category", category);

            List<Category> listCategories = categoryService.listCategoriesUsedInForm();
            model.addAttribute("listCategories", listCategories);

            redirectAttributes.addFlashAttribute("message","The category ID " + id + " has been deleted successfully");

            return "categories/category_form";

        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }

    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(
            @PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabled(id, enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String categoryDir = "../category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);

            String message = "The category ID: " + id + " has been deleted successfully";
            redirectAttributes.addFlashAttribute("message", message);
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Category> lisCategories = categoryService.listCategoriesUsedInForm();

        CategoryCsvExporter exporter = new CategoryCsvExporter();

        exporter.export(lisCategories, response);
    }

}
