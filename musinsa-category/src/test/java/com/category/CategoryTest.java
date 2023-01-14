package com.category;

import com.category.config.exception.GlobalException;
import com.category.entity.Category;
import com.category.service.CategoryService;
import com.category.vo.CategoryVo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class CategoryTest {

    @Autowired
    CategoryService categoryService;
    
    /**
     * select
     */
    @Test
    void test1() {
    	
        //전체 카테고리 조회
        String str = "";
        List<Category> categoryList = categoryService.selectCategory(str);
        
        Assertions.assertThat(categoryList.size()).isGreaterThan(0);

        
        //존재하는 특정 카테고리 조회
        str = "rice";
        categoryList = categoryService.selectCategory(str);
        
        Assertions.assertThat(categoryList.size()).isGreaterThan(0);

        
        //존재하지 않는 카테고리 조회
        str = "pizza";
        categoryList = categoryService.selectCategory(str);
        
        Assertions.assertThat(categoryList.size()).isZero();

    }
    
    /**
     * insert
     */
    @Test
    void test2()  throws Exception{
    	
        //입력받은 상위 카테고리가 존재하는 경우 하위 카테고리로 등록 처리
        CategoryVo categoryVo = new CategoryVo();
        categoryVo.setCategoryName("noodle");
        categoryVo.setParentId(1l);
        
        Long id = categoryService.insertCategory(categoryVo);
        
        Assertions.assertThat(id).isGreaterThan(0);

        //입력받은 상위 카테고리가 음수인 경우 최상위 카테고리로 등록
        CategoryVo categoryVo2 = new CategoryVo();
        categoryVo2.setCategoryName("cosmetic");
        categoryVo2.setParentId(-1l);
        
        Long id2 = categoryService.insertCategory(categoryVo2);
        
        Assertions.assertThat(id2).isGreaterThan(0);
        
        //입력받은 상위 카테고리가 존재하지 않는 경우 등록 실패
        CategoryVo categoryVo3 = new CategoryVo();
        categoryVo3.setCategoryName("noodle");
        categoryVo3.setParentId(99l);

        Assertions.assertThatThrownBy(() -> categoryService.insertCategory(categoryVo3)).isInstanceOf(GlobalException.class);
        
    }
    
    /**
     * udpate
     */
    @Test
    void test3() {
    	
    	CategoryVo vo = new CategoryVo();

        //정상 수정
        vo.setCategoryName("fashion");
        vo.setParentId(null);
        
        Category category = categoryService.updateCategory(vo, 2l);
        
        Assertions.assertThat(category.getCategoryName()).isEqualTo("fashion");
        
        
        //상위 카테고리를 하위 카테고리로 변경하려는 경우 실패
        vo.setCategoryName("shoes");
        vo.setParentId(6l);

        Assertions.assertThatThrownBy(() ->categoryService.updateCategory(vo, 2l)).isInstanceOf(GlobalException.class);
        
        //수정 대상 카테고리 ID가 없는 경우 실패
        Assertions.assertThatThrownBy(() ->categoryService.updateCategory(vo, 1000l)).isInstanceOf(GlobalException.class);

    }
    
    /**
     * delete
     */
    @Test
    void test4() {

        //하위 카테고리까지 전체 삭제
        Boolean isDeleteChild = true;
        
        Long id = 1l;
        
        categoryService.deleteCategory(id, isDeleteChild);
        int size = categoryService.selectCategory(null).size();
        
        Assertions.assertThat(size).isEqualTo(4);
        

        //지정 카테고리만 삭제
        isDeleteChild = false;
        
        id = 2l;
        
        categoryService.deleteCategory(id, isDeleteChild);
        size = categoryService.selectCategory(null).size();
        
        Assertions.assertThat(size).isEqualTo(3);

        
        //삭제할 카테고리가 없는 경우
        Assertions.assertThatThrownBy(() ->categoryService.deleteCategory(1000l, false)).isInstanceOf(GlobalException.class);
    }
}
