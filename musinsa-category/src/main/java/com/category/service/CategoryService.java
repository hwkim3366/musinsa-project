package com.category.service;

import java.util.List;

import com.category.entity.Category;
import com.category.vo.CategoryVo;

public interface CategoryService {
	
	public List<Category> selectCategory(String str);
	
	public Long insertCategory(CategoryVo vo);
	
	public Category updateCategory(CategoryVo vo, Long id);
	
	public void deleteCategory(Long id, Boolean isDeleteChild);
}
