package com.example.web01.Service;

import com.example.web01.Entity.DishEntity;
import com.example.web01.Repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {
    @Autowired
    private DishRepository dishRepository;

    //新しい料理を保存
    public DishEntity saveDish(DishEntity dish){
        return dishRepository.save(dish);   // save() メソッドで保存
    }

    // IDで料理を検索
    public Optional<DishEntity> getDishById(Long id) {
        return dishRepository.findById(id);  // findById() メソッドで検索
    }

    // 全料理を取得
    public List<DishEntity> getAllDishes() {
        return dishRepository.findAll();  // findAll() メソッドで全件取得
    }

    // IDで料理を削除
    public void deleteDishById(Long id) {
        dishRepository.deleteById(id);  // deleteById() メソッドで削除
    }

    public List<DishEntity> getDishesByCategoryId(Long categoryId){
        return dishRepository.findByCategoryId(categoryId);
    }

    public List<DishEntity> getDishesByCategoriesId(List<Long> categoryIds){
        return dishRepository.findByCategoryIdIn(categoryIds);
    }
}
