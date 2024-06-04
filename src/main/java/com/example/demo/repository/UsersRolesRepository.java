package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRolesRepository extends JpaRepository<User, Long> {

	@Modifying
	@Query(value = "DELETE FROM users_roles ur WHERE ur.user_id = :userId", nativeQuery = true)
	void deleteByUserId(@Param("userId") Long userId);

}
