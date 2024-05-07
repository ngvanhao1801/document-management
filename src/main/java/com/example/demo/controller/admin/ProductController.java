package com.example.demo.controller.admin;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProductController{
	
	@Value("${upload.path}")
	private String pathUploadImage;

	private final ProductRepository productRepository;

	private final CategoryRepository categoryRepository;
	
	private final UserRepository userRepository;
	
	@ModelAttribute(value = "user")
	public User user(Model model, Principal principal, User user) {

		if (principal != null) {
			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}

		return user;
	}

	public ProductController(CategoryRepository categoryRepository,
	                         ProductRepository productRepository, UserRepository userRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
	}

	// show list product - table list
	@ModelAttribute("products")
	public List<Product> showProduct(Model model) {
		List<Product> products = productRepository.findAll();
		model.addAttribute("products", products);

		return products;
	}

	@GetMapping(value = "/products")
	public String products(Model model, Principal principal) {
		Product product = new Product();
		model.addAttribute("product", product);

		return "admin/products";
	}

	// add product
	@PostMapping(value = "/addProduct")
	public String addProduct(@ModelAttribute("product") Product product, ModelMap model,
			@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {

		try {

			File convFile = new File(pathUploadImage + "/" + file.getOriginalFilename());
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {

		}

		product.setProductImage(file.getOriginalFilename());
		Product p = productRepository.save(product);
		if (null != p) {
			model.addAttribute("message", "Update success");
			model.addAttribute("product", product);
		} else {
			model.addAttribute("message", "Update failure");
			model.addAttribute("product", product);
		}
		return "redirect:/admin/products";
	}

	// show select option ở add product
	@ModelAttribute("categoryList")
	public List<Category> showCategory(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		return categoryList;
	}
	
	// get Edit brand
	@GetMapping(value = "/editProduct/{id}")
	public String editCategory(@PathVariable("id") Long id, ModelMap model) {
		Product product = productRepository.findById(id).orElse(null);
		
		model.addAttribute("product", product);

		return "admin/editProduct";
	}

	// delete category
	@GetMapping("/deleteProduct/{id}")
	public String delProduct(@PathVariable("id") Long id, Model model) {
		productRepository.deleteById(id);
		model.addAttribute("message", "Delete successful!");

		return "redirect:/admin/products";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}
}
