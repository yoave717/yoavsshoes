package com.shoestore.service.user;

import com.shoestore.entity.user.User;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.user.UserRepository;
import com.shoestore.service.base.BaseService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Long, UserRepository> {

    public UserService(UserRepository repository) {
        super(repository, "User");
    }

    @Override
    protected void updateEntityFields(User existingEntity, User newEntity) {
        // Additional update logic can be added here
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Search users with filters
     */
    public Page<User> searchUsers(String email, String firstName, String lastName, 
                                Boolean isAdmin, Boolean isActive, Pageable pageable) {
        return repository.searchUsers(email, firstName, lastName, isAdmin, pageable);
    }


    /**
     * Get user statistics for admin dashboard
     */
    public Map<String, Object> getUserStatistics() {
        Long totalUsers = count();
        Long adminCount = countByIsAdmin(true);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", totalUsers);
        statistics.put("adminUsers", adminCount);

        return statistics;
    }

    /**
     * Count users by admin status
     */
    public long countByIsAdmin(Boolean isAdmin) {
        return repository.countByIsAdmin(isAdmin);
    }

    // Additional user-specific methods can be added here
}
