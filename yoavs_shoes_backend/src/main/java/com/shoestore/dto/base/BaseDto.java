package com.shoestore.dto.base;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.view.Views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDto {

    @JsonView(Views.Summary.class)
    @EqualsAndHashCode.Include
    private Long id;

    @JsonView(Views.Detailed.class)
    private LocalDateTime createdAt;

    @JsonView(Views.Detailed.class)
    private LocalDateTime updatedAt;

    @JsonView(Views.Admin.class)
    private String createdBy;

    @JsonView(Views.Admin.class)
    private String updatedBy;

    @JsonView(Views.Detailed.class)
    private Long version;
}
