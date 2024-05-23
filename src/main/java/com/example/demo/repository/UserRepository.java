package com.example.demo.repository;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	@Query(value = "select u.* from `user` u\n" +
			"inner join users_roles ur on ur.user_id = u.user_id\n" +
			"inner join `role` r on ur.role_id = r.id\n" +
			"where r.name = 'ROLE_USER'", nativeQuery = true)
	List<User> findAllByRoles();

	@Query(value = "SELECT u.user_id, u.name, u.email, u.register_date, u.status, r.name as role_name " +
			"FROM `user` u " +
			"INNER JOIN users_roles ur ON ur.user_id = u.user_id " +
			"INNER JOIN `role` r ON ur.role_id = r.id " +
			"WHERE u.status = 1", nativeQuery = true)
	List<Object[]> findUsersByStatus();

}
