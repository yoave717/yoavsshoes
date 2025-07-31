package com.shoestore.dto.user;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.entity.user.UserAddress;
import org.springframework.stereotype.Component;

@Component
public class UserAddressMapper implements BaseCrudMapper<UserAddress, UserAddressDto,
        UserAddressDto.CreateAddressDto, UserAddressDto.UpdateAddressDto, UserAddressDto.AdminUpdateAddressDto> {

    @Override
    public UserAddressDto toDto(UserAddress entity) {
        if (entity == null) {
            return null;
        }

        UserAddressDto dto = UserAddressDto.builder()
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .isDefault(entity.getIsDefault())
                .label(entity.getLabel())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .deliveryInstructions(entity.getDeliveryInstructions())
                .formattedAddress(entity.getFormattedAddress())
                .shortFormattedAddress(entity.getShortFormattedAddress())
                .displayLabel(entity.getDisplayLabel())
                .isComplete(entity.isComplete())
                .build();

        // Map base entity fields
        mapBaseEntityToDto(entity, dto);

        return dto;
    }

    @Override
    public UserAddress toEntity(UserAddressDto dto) {
        if (dto == null) {
            return null;
        }

        UserAddress entity = UserAddress.builder()
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .isDefault(dto.getIsDefault())
                .label(dto.getLabel())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .deliveryInstructions(dto.getDeliveryInstructions())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    /** 
     * Convert UpdateAddressDto to UserAddress entity
     */
    @Override
    public UserAddress toEntity(UserAddressDto.UpdateAddressDto updateDto) {
        if (updateDto == null) {
            return null;
        }

        return UserAddress.builder()
                .addressLine1(updateDto.getAddressLine1())
                .addressLine2(updateDto.getAddressLine2())
                .city(updateDto.getCity())
                .state(updateDto.getState())
                .postalCode(updateDto.getPostalCode())
                .country(updateDto.getCountry())
                .isDefault(updateDto.getIsDefault() != null && updateDto.getIsDefault())
                .label(updateDto.getLabel())
                .firstName(updateDto.getFirstName())
                .lastName(updateDto.getLastName())
                .email(updateDto.getEmail())
                .phoneNumber(updateDto.getPhoneNumber())
                .deliveryInstructions(updateDto.getDeliveryInstructions())
                .build();
    }

    /**
     * Convert CreateAddressDto to UserAddress entity
     */
    @Override
    public UserAddress toEntity(UserAddressDto.CreateAddressDto dto) {
        if (dto == null) {
            return null;
        }

        return UserAddress.builder()
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .isDefault(dto.getIsDefault() != null && dto.getIsDefault())
                .label(dto.getLabel())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .deliveryInstructions(dto.getDeliveryInstructions())
                .build();
    }

    /**
     * Convert CreateOrderAddressInfoDto to UserAddress entity
     * 
     */
    public UserAddress toEntity(UserAddressDto.CreateOrderAddressInfoDto dto) {
        if (dto == null) {
            return null;
        }

        return UserAddress.builder()
                .id(dto.getId())
                .build();
                
    }
}
