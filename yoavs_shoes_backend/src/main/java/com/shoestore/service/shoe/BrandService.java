package com.shoestore.service.shoe;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoestore.entity.shoe.Brand;
import com.shoestore.repository.shoe.BrandRepository;
import com.shoestore.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional(readOnly = true)
public class BrandService extends BaseService<Brand, Long, BrandRepository> {


    public BrandService(BrandRepository brandRepository) {
        super(brandRepository, "Brand");
    }

    @Override
    protected void updateEntityFields(Brand existingEntity, Brand newEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateEntityFields'");
    }
}
