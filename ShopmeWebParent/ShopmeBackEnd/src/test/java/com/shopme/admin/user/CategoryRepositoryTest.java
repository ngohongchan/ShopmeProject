package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = categoryRepository.save(category);	
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(7);
		Category subcategory = new Category("Iphone", parent);
		Category savedCategory = categoryRepository.save(subcategory);
		
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
		
//		categoryRepository.saveAll(List.of(cameras, smartphone));
	}
	
	@Test
	public void testGetCategory() {
		Category category = categoryRepository.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		
		for(Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHiararchicalCategories() {
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		
		for(Category category : categories) {
			if(category.getParent() == null) {
				System.out.println(category.getName());
				
				Set<Category> children = category.getChildren();
				
				for(Category subCategory : children) {
					System.out.println("--" + subCategory.getName());
					printChildren(subCategory, 1);
				}
			}
		}
	}
	
	private void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			for(int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}
			
			System.out.println(subCategory.getName());
			
			printChildren(subCategory, newSubLevel);
		}
	}
	
	@Test
	public void testListRootCategories() {
		List<Category> rootCategory = categoryRepository.findRootCategory();
		rootCategory.forEach(category -> System.out.println(category.getName()));
	}

	@Test
	public void testFindByName() {
		String name = "Computers";
		Category category = categoryRepository.findByName(name);

		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
}
