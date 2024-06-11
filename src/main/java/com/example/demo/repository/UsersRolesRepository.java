package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UsersRolesRepository extends JpaRepository<User, Long> {

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM users_roles WHERE user_id = ?1", nativeQuery = true)
	void deleteByUserId(@Param("userId") Long userId);

}
