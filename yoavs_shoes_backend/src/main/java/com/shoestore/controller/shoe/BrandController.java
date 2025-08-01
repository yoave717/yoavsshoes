package com.shoestore.controller.shoe;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.shoe.BrandDto;
import com.shoestore.dto.shoe.BrandDto.CreateBrandDto;
import com.shoestore.dto.shoe.BrandDto.UpdateBrandDto;
import com.shoestore.dto.shoe.BrandMapper;
import com.shoestore.entity.shoe.Brand;
import com.shoestore.service.shoe.BrandService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/brands")
@Slf4j
@Tag(name = "Brands", description = "Brand management operations")
public class BrandController extends CrudController<
    Brand, Long, CreateBrandDto, UpdateBrandDto, BrandDto, BrandMapper, BrandService
> {

    protected BrandController(BrandService service, BrandMapper mapper) {
        super(service, "Brand", mapper);
    }

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"name", "createdAt", "updatedAt"};
    }

    @Override
    protected Class<Brand> getEntityClass() {
        return Brand.class;
    }
}
