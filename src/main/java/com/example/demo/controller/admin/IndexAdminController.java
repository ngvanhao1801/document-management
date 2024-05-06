package com.example.demo.controller.admin;

import com.example.demo.dto.ChartDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
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
		long countOrders = orderRepository.count();
		return ResponseEntity.ok(countOrders);
	}

	// dem so luong sp
	@GetMapping("/api/admin/count/products")
	public ResponseEntity<Object> getCountProduct(){
		long countProducts = productRepository.count();
		return ResponseEntity.ok(countProducts);
	}

//	@GetMapping("/api/admin/statistics")
//	public ResponseEntity<Object> getStatistic30Day(){
//		List<StatisticDTO> statistics = statisticRepository.getStatistic30Day();
//		return ResponseEntity.ok(statistics);
//	}
//
//	@PostMapping("/api/admin/statistics")
//	public ResponseEntity<Object> getStatisticDayByDay(@RequestBody FilterDayByDay filterDayByDay){
//		List<StatisticDTO> statisticDTOS = statisticRepository.getStatisticDayByDay(filterDayByDay.getToDate(),filterDayByDay.getFromDate());
//		return ResponseEntity.ok(statisticDTOS);
//	}

	@GetMapping("/api/admin/product-order-categories")
	public ResponseEntity<Object> getListProductOrderCategories(){
		List<ChartDTO> chartDTOS = categoryRepository.getListProductOrderCategories();
		return ResponseEntity.ok(chartDTOS);
	}

	@GetMapping("/api/admin/product-order")
	public ResponseEntity<Object> getProductOrder(){
		Pageable pageable = PageRequest.of(0,10);
		Date date = new Date();
		List<ChartDTO> chartDTOS = productRepository.getProductOrders(pageable, date.getMonth() +1, date.getYear() + 1900);
		return ResponseEntity.ok(chartDTOS);
	}
}
