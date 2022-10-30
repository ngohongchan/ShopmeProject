package com.shopme.admin.user;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> listsAll() {
		List<Category> rootCategories = categoryRepository.findRootCategory();
		return listHierarchicalCategory(rootCategories);
	}

	private List<Category> listHierarchicalCategory(List<Category> rootCategories) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for(Category rooCategory: rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rooCategory));

			Set<Category> children = rooCategory.getChildren();

			for(Category subCategory: children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(Category.copyFull(subCategory), name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel) {
		Set<Category> children = parent.getChildren();
		int newSubLevel = subLevel + 1;

		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}

			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
		}
	}

	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUsedInForm = new ArrayList<>();

		List<Category> categoriesInDB = (List<Category>) categoryRepository.findAll();

		for(Category category : categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));

				Set<Category> children = category.getChildren();

				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					listChildren(categoriesUsedInForm, subCategory, 1);
				}
			}
		}

		return categoriesUsedInForm;
	}

	private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();

		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}

			name += subCategory.getName();

			System.out.println(name);
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

			listChildren(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return  categoryRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any user with Id "+ id);
		}
	}

	
}
