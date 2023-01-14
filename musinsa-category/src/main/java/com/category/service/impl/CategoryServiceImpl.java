package com.category.service.impl;

import com.category.config.exception.GlobalException;
import com.category.config.exception.GlobalExceptionEnumCode;
import com.category.entity.Category;
import com.category.repository.CategoryRepository;
import com.category.repository.CategorySpecification;
import com.category.service.CategoryService;
import com.category.vo.CategoryVo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{
    
	private CategoryRepository categoryRepository;
	
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
    	
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * select
     * 카테고리명 like 검색
     */
    public List<Category> selectCategory(String str) {
    	
        Specification<Category> specification = Specification.where(CategorySpecification.noParameter());
        
        //파라미터 str이 null이 아닌 경우 카테고리명 기준으로 like 검색
        if(str != null && !str.trim().isEmpty()) {
        	specification = specification.and(CategorySpecification.likeSelect(str));
        }
        
        return categoryRepository.findAll(specification);
    }
    
    /**
     * insert
     */
    public Long insertCategory(CategoryVo vo) {
    	
    	//상위 카테고리 ID, 카테고리명 필수 입력 체크
    	if(vo.getParentId() == null || vo.getCategoryName() == null || vo.getCategoryName().trim().isEmpty()) {
    		throw new GlobalException(GlobalExceptionEnumCode.VALIDATION_ERROR, GlobalExceptionEnumCode.VALIDATION_ERROR.getMessage());
        }
    	
    	Category newCategory  = new Category();
    	
    	//상위 카테고리 값이 임의의 음수로 넘어오는 경우 신규 최상위 카테고리로 생성 ex) -1
    	if(vo.getParentId() < 0) {
    		
    		//신규 최상위 카테고리 ID로 셋팅 (parent_id : null, ID는 자동 채번)
        	newCategory.setParentId(null);
        	
    	} else {
    		
    		//지정한 상위 카테고리 값의 존재 여부 파악
            Category category = this.isExist(vo.getParentId());
            
            //지정한 상위 카테고리 ID가 존재하는 경우
            if (category != null ) {
            	
            	//해당 상위 카테고리 ID의 하위 카테고리 ID로 생성 (ID는 자동 채번)
            	newCategory.setParentId(vo.getParentId());
            	
            } else { //지정한 상위 카테고리 ID가 없는 경우
            	
            	throw new GlobalException(GlobalExceptionEnumCode.NOT_FOUND_PARENT_CATEGORY, GlobalExceptionEnumCode.NOT_FOUND_PARENT_CATEGORY.getMessage());
            }
    	}
        
        newCategory.setCategoryName(vo.getCategoryName());
        
        return categoryRepository.save(newCategory).getId();
    }
    
    /**
     * update
     */
    public Category updateCategory(CategoryVo vo, Long id) {
    	
    	//카테고리 ID, 카테고리명 필수 입력 체크
    	if(id == null || vo.getCategoryName() == null || vo.getCategoryName().trim().isEmpty()) {
    		throw new GlobalException(GlobalExceptionEnumCode.VALIDATION_ERROR, GlobalExceptionEnumCode.VALIDATION_ERROR.getMessage());
        }
    	
        Category category =  this.isExist(id);
        
        //존재하는 카테고리 ID인 경우
        if (category != null){
        	
           //상위 카테고리를 수정 시
           if(vo.getParentId()!= null){
        	   
        	   //자기 자신의 하위 카테고리로는 수정 불가(나머지 하위 카테고리들의 상위 카테고리값이 없어져 hirachey 구조가 깨지기 때문)
               category.getChild().forEach(o->{
            	   
                    if (vo.getParentId().equals(o.getId())){
                        throw new GlobalException(GlobalExceptionEnumCode.CAN_NOT_UPDATE, GlobalExceptionEnumCode.CAN_NOT_UPDATE.getMessage());
                    }
               });
               
               //상위 카테고리 값이 임의의 음수로 넘어오는 경우 신규 최상위 카테고리로 수정 ex) -1
               if(vo.getParentId() < 0) {
            	   //신규 최상위 카테고리 ID로 셋팅 (parent_id : null, ID는 자동 채번)
            	   category.setParentId(null);
               } else {
            	   category.setParentId(vo.getParentId());
               }
           }

           category.setCategoryName(vo.getCategoryName());

           return categoryRepository.findById(category.getId()).get();

       } else { //없는 카테고리 ID값인 경우
           throw new GlobalException(GlobalExceptionEnumCode.NOT_FOUND_DATA, GlobalExceptionEnumCode.NOT_FOUND_DATA.getMessage());
       }
    }
    
    /**
     * delete
     * @param id
     * @param isDeleteChild : 하위 카테고리 전체 삭제(true) or 지정 카테고리만 삭제(false)
     */
    public void deleteCategory(Long id, Boolean isDeleteChild) {
    	
    	//카테고리 ID 필수 입력 체크
    	if(id == null) {
    		throw new GlobalException(GlobalExceptionEnumCode.VALIDATION_ERROR, GlobalExceptionEnumCode.VALIDATION_ERROR.getMessage());
        }
    	
        Category category = this.isExist(id);

        if (category != null){

            List<Category> categoryList = categoryRepository.findAllByParentId(id);
            
            if (categoryList.size() > 0) {
                if (!isDeleteChild) {
                	categoryList.forEach(o -> {o.setParentId(category.getParentId() == null ? null : category.getParentId()); categoryRepository.flush();});
                	
                } else {
                	categoryList.forEach(o -> {deleteChild(o.getId());});
                }
            }

            categoryRepository.deleteById(category.getId());
            
        }else {
            throw new GlobalException(GlobalExceptionEnumCode.NOT_FOUND_DATA, GlobalExceptionEnumCode.NOT_FOUND_DATA.getMessage());
        }
    }
    
    /**
     * 하위 카테고리 삭제
     * @param id
     */
    public void deleteChild(Long id) {
    	
    	categoryRepository.deleteById(id);
        
        List<Category> categoryList = categoryRepository.findAllByParentId(id);
        
        if (categoryList == null || categoryList.size() == 0) {
            return;
        }
        
        categoryList.forEach(o -> {categoryRepository.delete(o); deleteChild(o.getId());});
    }
    
    /**
     * 카테고리 데이터 존재 여부 파악
     */
    public Category isExist(Long id) {
    	
        Optional<Category> categoryList = categoryRepository.findById(id);
        
        if (categoryList.isPresent()){
            return categoryList.get();
        } else {
            return null;
        }
    }
}
