package com.shoestore.service.user;

import com.shoestore.dto.user.UserAddressDto;
import com.shoestore.dto.user.UserAddressMapper;
import com.shoestore.entity.user.User;
import com.shoestore.entity.user.UserAddress;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.user.UserAddressRepository;
import com.shoestore.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user addresses
 */
@Service
@Slf4j
@Transactional
public class UserAddressService extends BaseService<UserAddress, Long, UserAddressRepository> {

    private final UserService userService;
    private final UserAddressMapper userAddressMapper;

    /**
     * Constructor
     */
    public UserAddressService(UserAddressRepository userAddressRepository,
                            UserService userService,
                            UserAddressMapper userAddressMapper) {
        super(userAddressRepository, "user address");
        this.userService = userService;
        this.userAddressMapper = userAddressMapper;
    }

    /**
     * Get all addresses for a user (with User ID - validates user exists)
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getUserAddresses(Long userId) {
        log.debug("Getting addresses for user: {}", userId);

        // Validate user exists first
        User user = userService.getById(userId);

        return getUserAddresses(user);
    }

    /**
     * Get all addresses for a user (with User object - no validation needed)
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getUserAddresses(User user) {
        log.debug("Getting addresses for user: {}", user.getId());

        return repository.findByUserIdOrderByDefaultAndCreatedAt(user.getId());
    }


    /**
     * Get default address for a user (with User ID - validates user exists)
     */
    @Transactional(readOnly = true)
    public Optional<UserAddress> getDefaultAddress(Long userId) {
        log.debug("Getting default address for user: {}", userId);

        // Validate user exists first
        User user = userService.getById(userId);

        return getDefaultAddress(user);
    }

    /**
     * Get default address for a user (with User object - no validation needed)
     */
    @Transactional(readOnly = true)
    public Optional<UserAddress> getDefaultAddress(User user) {
        log.debug("Getting default address for user: {}", user.getId());

        return Optional.ofNullable(user.getDefaultAddress());
    }

    /**
     * Get a specific address by ID and user ID (with User ID - validates user exists)
     */
    @Transactional(readOnly = true)
    public UserAddress getAddress(Long userId, Long addressId) {
        log.debug("Getting address {} for user: {}", addressId, userId);

        // Validate user exists first
        User user = userService.getById(userId);

        return getAddress(user, addressId);
    }

    /**
     * Get a specific address by ID and user (with User object - no validation needed)
     */
    @Transactional(readOnly = true)
    public UserAddress getAddress(User user, Long addressId) {
        log.debug("Getting address {} for user: {}", addressId, user.getId());

        return repository.findById(addressId)
                .filter(addr -> addr.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
    }

    /**
     * Create a new address for a user (with User ID - fetches user from DB)
     */
    public UserAddress createAddress(Long userId, UserAddressDto.CreateAddressDto request) {
        log.debug("Creating new address for user: {}", userId);

        User user = userService.getById(userId);

        return createAddress(user, request);
    }

    /**
     * Create a new address for a user (with User object - no DB fetch needed)
     */
    public UserAddress createAddress(User user, UserAddressDto.CreateAddressDto request) {
        log.debug("Creating new address for user: {}", user.getId());

        UserAddress address = userAddressMapper.toEntity(request);
        address.setUser(user);

        // If this is marked as default, clear other default addresses
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultAddressForUser(user.getId());
        }

        // Persist the address
        UserAddress savedAddress = repository.save(address);

        log.info("Created new address with id: {} for user: {}", savedAddress.getId(), user.getId());

        return savedAddress;
    }

    /**
     * Update an existing address (with User ID - validates user exists)
     */
    public UserAddress updateAddress(Long userId, Long addressId, UserAddressDto.UpdateAddressDto request) {
        log.debug("Updating address {} for user: {}", addressId, userId);

        // Validate user exists first
        User user = userService.getById(userId);

        return updateAddress(user, addressId, request);
    }

    /**
     * Update an existing address (with User object - no validation needed)
     */
    public UserAddress updateAddress(User user, Long addressId, UserAddressDto.UpdateAddressDto request) {
        log.debug("Updating address {} for user: {}", addressId, user.getId());

        UserAddress address = repository.findById(addressId)
                .filter(addr -> addr.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // If this is being marked as default, clear other default addresses
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            clearDefaultAddressForUser(user.getId());
        }

        updateEntityFromRequest(address, request);
        UserAddress savedAddress = repository.save(address);

        log.info("Updated address with id: {} for user: {}", addressId, user.getId());

        return savedAddress;
    }

    /**
     * Delete an address (with User ID - validates user exists)
     */
    public void deleteAddress(Long userId, Long addressId) {
        log.debug("Deleting address {} for user: {}", addressId, userId);

        // Validate user exists first
        User user = userService.getById(userId);

        deleteAddress(user, addressId);
    }

    /**
     * Delete an address (with User object - no validation needed)
     */
    public void deleteAddress(User user, Long addressId) {
        log.debug("Deleting address {} for user: {}", addressId, user.getId());

        UserAddress address = repository.findById(addressId)
                .filter(addr -> addr.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        repository.delete(address);
        log.info("Deleted address with id: {} for user: {}", addressId, user.getId());
    }

    /**
     * Set an address as default (with User ID - validates user exists)
     */
    public UserAddress setDefaultAddress(Long userId, Long addressId) {
        log.debug("Setting address {} as default for user: {}", addressId, userId);

        // Validate user exists first
        User user = userService.getById(userId);

        return setDefaultAddress(user, addressId);
    }

    /**
     * Set an address as default (with User object - no validation needed)
     */
    public UserAddress setDefaultAddress(User user, Long addressId) {
        log.debug("Setting address {} as default for user: {}", addressId, user.getId());

        UserAddress address = repository.findById(addressId)
                .filter(addr -> addr.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // Clear other default addresses
        clearDefaultAddressForUser(user.getId());

        // Set this address as default
        address.setIsDefault(true);
        UserAddress savedAddress = repository.save(address);

        log.info("Set address {} as default for user: {}", addressId, user.getId());

        return savedAddress;
    }

    /**
     * Clear default address for a user (set all to non-default)
     */
    private void clearDefaultAddressForUser(Long userId) {
        log.debug("Clearing default addresses for user: {}", userId);
        repository.clearDefaultAddressForUser(userId);
    }

    /**
     * Get address count for a user (with User ID - validates user exists)
     */
    @Transactional(readOnly = true)
    public long getAddressCount(Long userId) {
        // Validate user exists first
        User user = userService.getById(userId);

        return getAddressCount(user);
    }

    /**
     * Get address count for a user (with User object - no validation needed)
     */
    @Transactional(readOnly = true)
    public long getAddressCount(User user) {
        return repository.countByUserId(user.getId());
    }

    /**
     * Check if user has any addresses (with User ID - validates user exists)
     */
    @Transactional(readOnly = true)
    public boolean hasAddresses(Long userId) {
        // Validate user exists first
        User user = userService.getById(userId);

        return hasAddresses(user);
    }

    /**
     * Check if user has any addresses (with User object - no validation needed)
     */
    @Transactional(readOnly = true)
    public boolean hasAddresses(User user) {
        return getAddressCount(user) > 0;
    }

    /**
     * Check if user has a default address (with User ID - validates user exists)
     */
    @Transactional(readOnly = true)
    public boolean hasDefaultAddress(Long userId) {
        // Validate user exists first
        User user = userService.getById(userId);

        return hasDefaultAddress(user);
    }

    /**
     * Check if user has a default address (with User object - no validation needed)
     */
    @Transactional(readOnly = true)
    public boolean hasDefaultAddress(User user) {
        return repository.existsByUserIdAndIsDefaultTrue(user.getId());
    }


    /**
     * Update entity fields from another entity (required by BaseService)
     */
    @Override
    protected void updateEntityFields(UserAddress target, UserAddress source) {
        target.setAddressLine1(source.getAddressLine1());
        target.setAddressLine2(source.getAddressLine2());
        target.setCity(source.getCity());
        target.setState(source.getState());
        target.setPostalCode(source.getPostalCode());
        target.setCountry(source.getCountry());
        if (source.getIsDefault() != null) {
            target.setIsDefault(source.getIsDefault());
        }
        target.setLabel(source.getLabel());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setDeliveryInstructions(source.getDeliveryInstructions());
    }

    /**
     * Find address by ID with user relationship eagerly loaded
     * This method is used by the access control aspect to avoid lazy loading issues
     */
    @Transactional(readOnly = true)
    public Optional<UserAddress> findByIdWithUser(Long id) {
        log.debug("Finding address with user for access validation: {}", id);
        return repository.findByIdWithUser(id);
    }

    /**
     * Generic method for access control - delegates to findByIdWithUser
     * This follows the generic pattern expected by AccessControlAspect
     */
    @Transactional(readOnly = true) 
    public Optional<UserAddress> findByIdWithOwner(Long id) {
        log.debug("Finding address with owner (user) for access validation: {}", id);
        return findByIdWithUser(id);
    }

                

    /**
     * Update entity from UpdateAddressDto
     */
    private void updateEntityFromRequest(UserAddress entity, UserAddressDto.UpdateAddressDto dto) {
        if (dto == null || entity == null) {
            return;
        }

        // Use Java 8 Optional for cleaner null checks and assignments
        Optional.ofNullable(dto.getAddressLine1()).ifPresent(entity::setAddressLine1);
        Optional.ofNullable(dto.getAddressLine2()).ifPresent(entity::setAddressLine2);
        Optional.ofNullable(dto.getCity()).ifPresent(entity::setCity);
        Optional.ofNullable(dto.getState()).ifPresent(entity::setState);
        Optional.ofNullable(dto.getPostalCode()).ifPresent(entity::setPostalCode);
        Optional.ofNullable(dto.getCountry()).ifPresent(entity::setCountry);
        Optional.ofNullable(dto.getIsDefault()).ifPresent(entity::setIsDefault);
        Optional.ofNullable(dto.getLabel()).ifPresent(entity::setLabel);
        Optional.ofNullable(dto.getFirstName()).ifPresent(entity::setFirstName);
        Optional.ofNullable(dto.getLastName()).ifPresent(entity::setLastName);
        Optional.ofNullable(dto.getEmail()).ifPresent(entity::setEmail);
        Optional.ofNullable(dto.getPhoneNumber()).ifPresent(entity::setPhoneNumber);
        Optional.ofNullable(dto.getDeliveryInstructions()).ifPresent(entity::setDeliveryInstructions);
    }
}
