package com.shoestore.dto.user;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseCrudMapper<User, UserDto, 
        UserDto.CreateUserDto, UserDto.UpdateUserDto, UserDto.AdminUpdateUserDto> {

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        UserDto dto = UserDto.builder()
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .isAdmin(entity.getIsAdmin())
                .lastLogin(entity.getLastLogin())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .accountLocked(entity.isAccountLocked())
                .accountLockedUntil(entity.getAccountLockedUntil())
                .build();

        // Map base entity fields
        mapBaseEntityToDto(entity, dto);

        return dto;
    }

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User entity = User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .isAdmin(dto.getIsAdmin())
                .lastLogin(dto.getLastLogin())
                .failedLoginAttempts(dto.getFailedLoginAttempts())
                .accountLockedUntil(dto.getAccountLockedUntil())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }


    /**
     * Convert CreateUserDto to User entity
     */
    public User toEntity(UserDto.CreateUserDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .isAdmin(false) // Default to non-admin
                .failedLoginAttempts(0)
                .build();
    }

    /**
     * Update User entity from UpdateUserDto
     */
    public void updateFromDto(UserDto.UpdateUserDto dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null) {
            entity.setPhoneNumber(dto.getPhoneNumber());
        }
    }

    /**
     * Update User entity from AdminUpdateUserDto
     */
    public void updateFromAdminDto(UserDto.AdminUpdateUserDto dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null) {
            entity.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getIsAdmin() != null) {
            entity.setIsAdmin(dto.getIsAdmin());
        }
        if (dto.getFailedLoginAttempts() != null) {
            entity.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        }
        if (dto.getAccountLockedUntil() != null) {
            entity.setAccountLockedUntil(dto.getAccountLockedUntil());
        }
    }
}
