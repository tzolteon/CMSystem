package org.example.repositories;

import org.example.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.user.username = :username")
    List<String> findRolesByUsername(String username);
}
