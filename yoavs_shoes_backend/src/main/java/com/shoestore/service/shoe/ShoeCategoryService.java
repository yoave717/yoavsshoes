package com.shoestore.service.shoe;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoestore.entity.shoe.ShoeCategory;
import com.shoestore.repository.shoe.ShoeCategoryRepository;
import com.shoestore.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ShoeCategoryService extends BaseService<ShoeCategory, Long, ShoeCategoryRepository> {

    public ShoeCategoryService(ShoeCategoryRepository repository) {
        super(repository, "Category");
    }

    @Override
    protected void updateEntityFields(ShoeCategory existingEntity, ShoeCategory newEntity) {
        existingEntity.setName(newEntity.getName());
        existingEntity.setDescription(newEntity.getDescription());
    }

    // Additional methods for category management can be added here
    
}
