package com.shopme.admin.user.service;

import java.util.*;

import javax.transaction.Transactional;

import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.user.exceprion.CategoryNotFoundException;
import com.shopme.admin.user.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> listsByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {

		Sort sort = Sort.by("name");

		if(sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if(sortDir.equals("desc")) {
			sort = sort.descending();
		}

		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

		Page<Category> pageCategories = null;

		if(keyword != null && !keyword.isEmpty()) {
			pageCategories = categoryRepository.search(keyword, pageable);
		} else {
			pageCategories = categoryRepository.findRootCategories(pageable);
		}

		List<Category> rootCategories = pageCategories.getContent();

		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());

		if(keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();

			for(Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}

			return searchResult;
		} else {
			return listHierarchicalCategory(rootCategories, sortDir);
		}
	}

	private List<Category> listHierarchicalCategory(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for(Category rooCategory: rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rooCategory));

			Set<Category> children = sortSubcategories(rooCategory.getChildren(), sortDir);

			for(Category subCategory: children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(Category.copyFull(subCategory), name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubcategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;

		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}

			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}
	}

	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUsedInForm = new ArrayList<>();

		List<Category> categoriesInDB = (List<Category>) categoryRepository.findRootCategories(Sort.by("name").ascending());

		for(Category category : categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));

				Set<Category> children = sortSubcategories(category.getChildren(), "asc");

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
		Set<Category> children = sortSubcategories(parent.getChildren(), "asc");

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

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Category categoryByName = categoryRepository.findByName(name);

		if(isCreatingNew) {
			if(categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);

				if(categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if(categoryByName != null && !categoryByName.getId().equals(id)) {
				return "DuplicateName";
			}

			Category categoryByAlias = categoryRepository.findByAlias(alias);
			if(categoryByAlias != null && !categoryByAlias.getId().equals(id)) {
				return "DuplicateAlias";
			}
		}

		return "OK";
	}

	private SortedSet<Category> sortSubcategories(Set<Category> children) {
		return sortSubcategories(children, "asc");
	}

	private SortedSet<Category> sortSubcategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category cat1, Category cat2) {

				if(sortDir == "asc") {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});

		sortedChildren.addAll(children);


		return sortedChildren;
	}

	public void updateCategoryEnabled(Integer id, boolean enabled) {
		categoryRepository.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = categoryRepository.countById(id);

		if(countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}

		categoryRepository.deleteById(id);
	}
}
