package com.example.demo.controller.admin;

import com.example.demo.dto.ChartDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
//@RequestMapping("/admin")
public class IndexAdminController{
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	PendingDocumentRepository pendingDocumentRepository;

	@ModelAttribute(value = "user")
	public User user(Model model, Principal principal, User user) {

		if (principal != null) {
			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}

		return user;
	}

	@GetMapping(value = "/admin/home")
	public String index() {
		return "admin/index";
	}

	// dem so luong category
	@GetMapping("/api/admin/count/categories")
	public ResponseEntity<Object> getCountCategories(){
		long countCategories = categoryRepository.count();
		return ResponseEntity.ok(countCategories);
	}

	// dem so luong user
	@GetMapping("/api/admin/count/users")
	public ResponseEntity<Object> getCountUsers(){
		long countUsers = userRepository.count();
		return ResponseEntity.ok(countUsers);
	}

	// dem so luong don hang
	@GetMapping("/api/admin/count/orders")
	public ResponseEntity<Object> getCountOrders(){
		long countDocumentPending = pendingDocumentRepository.countAllByDocumentStatus();
		return ResponseEntity.ok(countDocumentPending);
	}

	// dem so luong sp
	@GetMapping("/api/admin/count/products")
	public ResponseEntity<Object> getCountProduct(){
		long countDocuments = documentRepository.count();
		return ResponseEntity.ok(countDocuments);
	}

	@GetMapping("/api/admin/product-order-categories")
	public ResponseEntity<Object> getListProductOrderCategories(){
		List<ChartDTO> chartDTOS = categoryRepository.getListProductOrderCategories();
		return ResponseEntity.ok(chartDTOS);
	}

	@GetMapping("/api/admin/product-order")
	public ResponseEntity<Object> getDocumentFavourite(){
		List<ChartDTO> chartDTOS = documentRepository.getDocumentByFavorite();
		return ResponseEntity.ok(chartDTOS);
	}
}
