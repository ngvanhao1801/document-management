package com.example.demo.controller.admin;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UsersRolesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

	private final UserRepository userRepository;

	private final FavoriteRepository favoriteRepository;

	private final UsersRolesRepository usersRolesRepository;

	public UserController(UserRepository userRepository,
												FavoriteRepository favoriteRepository,
												UsersRolesRepository usersRolesRepository) {
		this.userRepository = userRepository;
		this.favoriteRepository = favoriteRepository;
    this.usersRolesRepository = usersRolesRepository;
  }

	@GetMapping(value = "/admin/users")
	public String customer(Model model, Principal principal) {

		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<Object[]> results = userRepository.findUsersByStatus();
		List<UserDto> users = results.stream()
				.map(result -> new UserDto(
						((Number) result[0]).longValue(),
						(String) result[1],
						(String) result[2],
						(Date) result[3],
						(Boolean) result[4],
						(String) result[5]))
				.collect(Collectors.toList());
		model.addAttribute("users", users);

		return "/admin/users";
	}

	@PostMapping(value = "/admin/users/toggleStatus")
	public ResponseEntity<Map<String, Object>> toggleUserStatus(@RequestBody Map<String, Object> request) {
		Long userId = Long.valueOf(request.get("userId").toString());

		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			boolean currentStatus = user.isStatus();
			boolean newStatus = !currentStatus;
			user.setStatus(newStatus);

			userRepository.save(user);

			// Tạo phản hồi thành công
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("newStatus", newStatus);
			return ResponseEntity.ok(response);
		} else {
			// Tạo phản hồi khi không tìm thấy người dùng
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "User not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	@Transactional
	@GetMapping(value = "/admin/users/delete/{userId}")
	public String deleteUser(@PathVariable("userId") Long userId) {
		favoriteRepository.deleteByUser_UserId(userId);

		usersRolesRepository.deleteByUserId(userId);

		userRepository.deleteById(userId);

		return "redirect:/admin/users";
	}

}
