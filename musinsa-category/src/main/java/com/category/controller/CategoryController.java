package com.category.controller;

import com.category.entity.Category;
import com.category.service.CategoryService;
import com.category.vo.CategoryVo;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryController {
	
	private CategoryService categoryService;
	
    public CategoryController(CategoryService categoryService) {
    	
        this.categoryService = categoryService;
    }
    
    /**
     * select
     * @param str
     * @return
     */
    @GetMapping("/category")
    public List<Category> selectCategory(@Nullable String str) {
    	
        List<Category> categories =categoryService.selectCategory(str);
        return categories;
    }
    
    /**
     * insert
     * @param vo
     */
    @PostMapping("/category")
    public void insertCategory(@RequestBody @Valid CategoryVo vo) {
    	
        categoryService.insertCategory(vo);
    }
    
    /**
     * update
     * @param vo
     * @param id
     */
    @PutMapping("/category/{id}")
    public void updateCategory(@RequestBody @Valid CategoryVo vo, @PathVariable("id") Long id) {
    	
        categoryService.updateCategory(vo, id);
    }
    
    /**
     * delete
     * @param id
     * @param isDeleteChild
     */
    @DeleteMapping("/category/{id}")
    public void deleteCategory(@PathVariable("id") Long id, @RequestParam("isDeleteChild") Boolean isDeleteChild ) {
        
    	categoryService.deleteCategory( id, isDeleteChild);
    }
}
