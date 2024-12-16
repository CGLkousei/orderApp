package com.example.web01.Service;

import com.example.web01.Entity.CategoryEntity;
import com.example.web01.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    //新しいカテゴリを保存
    public CategoryEntity saveCategory(CategoryEntity category){
        return categoryRepository.save(category);   // save() メソッドで保存
    }

    // IDでカテゴリを検索
    public Optional<CategoryEntity> getCategoryById(Long id) {
        return categoryRepository.findById(id);  // findById() メソッドで検索
    }

    // 全カテゴリを取得
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();  // findAll() メソッドで全件取得
    }

    // IDでカテゴリを削除
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);  // deleteById() メソッドで削除
    }

    public List<CategoryEntity> getCategoriesByRestaurantId(Long restaurantId){
        return categoryRepository.findByRestaurantId(restaurantId);
    }
}
