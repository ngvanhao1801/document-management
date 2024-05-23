package com.example.demo.controller.admin;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
