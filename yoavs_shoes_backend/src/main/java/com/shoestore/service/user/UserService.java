package com.shoestore.service.user;

import com.shoestore.entity.user.User;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.user.UserRepository;
import com.shoestore.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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
        Object[] stats = repository.getUserStatistics();
        
        Map<String, Object> statistics = new HashMap<>();
        if (stats != null && stats.length >= 4) {
            // Convert BigInteger to Long for consistency
            statistics.put("totalUsers", stats[0] instanceof BigInteger ? ((BigInteger) stats[0]).longValue() : stats[0]);
            statistics.put("activeUsers", stats[1] instanceof BigInteger ? ((BigInteger) stats[1]).longValue() : stats[1]);
            statistics.put("adminUsers", stats[2] instanceof BigInteger ? ((BigInteger) stats[2]).longValue() : stats[2]);
            statistics.put("verifiedUsers", stats[3] instanceof BigInteger ? ((BigInteger) stats[3]).longValue() : stats[3]);
        } else {
            // Fallback values
            statistics.put("totalUsers", 0L);
            statistics.put("activeUsers", 0L);
            statistics.put("adminUsers", 0L);
            statistics.put("verifiedUsers", 0L);
        }
        
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
