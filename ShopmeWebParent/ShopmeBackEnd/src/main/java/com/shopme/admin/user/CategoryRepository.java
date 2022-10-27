package com.shopme.admin.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
}
