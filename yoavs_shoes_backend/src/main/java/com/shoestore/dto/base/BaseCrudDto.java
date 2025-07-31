package com.shoestore.dto.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base CRUD DTO with standard Create and Update inner classes
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class BaseCrudDto extends BaseDto {

    /**
     * Base Create DTO class
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static abstract class CreateDto {
        // Common create fields can be added here
    }

    /**
     * Base Update DTO class
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static abstract class UpdateDto {
        // Common update fields can be added here
    }

    /**
     * Base Admin Update DTO class
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static abstract class AdminUpdateDto extends UpdateDto {
        // Common admin update fields can be added here
    }
}
