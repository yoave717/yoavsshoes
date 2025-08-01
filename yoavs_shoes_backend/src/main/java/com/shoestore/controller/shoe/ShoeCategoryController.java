package com.shoestore.controller.shoe;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.shoe.ShoeCategoryDto;
import com.shoestore.dto.shoe.ShoeCategoryDto.CreateShoeCategoryDto;
import com.shoestore.dto.shoe.ShoeCategoryDto.UpdateShoeCategoryDto;
import com.shoestore.dto.shoe.ShoeCategoryMapper;
import com.shoestore.entity.shoe.ShoeCategory;
import com.shoestore.service.shoe.ShoeCategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/shoe-categories")
@Slf4j
@Tag(name = "Shoe Categories", description = "Shoe category management operations")
public class ShoeCategoryController extends CrudController<
    ShoeCategory, Long, CreateShoeCategoryDto, UpdateShoeCategoryDto, ShoeCategoryDto, ShoeCategoryMapper, ShoeCategoryService>{

    protected ShoeCategoryController(ShoeCategoryService service, ShoeCategoryMapper mapper) {
        super(service, "ShoeCategory", mapper);
    }

    @Override
    protected String[] getAllowedSortFields() {
        return new String[] {"name", "description"};
    }

    @Override
    protected Class<ShoeCategory> getEntityClass() {
        return ShoeCategory.class;
    }

}