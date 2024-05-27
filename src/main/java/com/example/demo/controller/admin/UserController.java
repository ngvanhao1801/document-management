package com.example.demo.controller.admin;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

  @Autowired
  UserRepository userRepository;

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
    boolean newStatus = Boolean.parseBoolean(request.get("status").toString());

    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.setStatus(newStatus);  // Cập nhật trạng thái người dùng
      userRepository.save(user);

      // Tạo phản hồi thành công
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      return ResponseEntity.ok(response);
    } else {
      // Tạo phản hồi khi không tìm thấy người dùng
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "User not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping(value = "/admin/users/delete/{id}")
  @Transactional
  public String deleteUser(@PathVariable("id") Long id) {
    Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.setStatus(false);
      userRepository.save(user);
    }

    return "redirect:/admin/users";
  }

}
