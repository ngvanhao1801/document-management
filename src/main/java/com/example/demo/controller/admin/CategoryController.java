package com.example.demo.controller.admin;

import com.example.demo.entity.Category;
import com.example.demo.entity.User;
import com.example.demo.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class CategoryController {

	private final CategoryRepository categoryRepository;

	private final FolderRepository folderRepository;

	private final DocumentRepository documentRepository;

	private final FavoriteRepository favoriteRepository;

	private final PendingDocumentRepository pendingDocumentRepository;

	private final UserRepository userRepository;

	public CategoryController(CategoryRepository categoryRepository, FolderRepository folderRepository, DocumentRepository documentRepository, FavoriteRepository favoriteRepository, PendingDocumentRepository pendingDocumentRepository, UserRepository userRepository) {
		this.categoryRepository = categoryRepository;
    this.folderRepository = folderRepository;
    this.documentRepository = documentRepository;
    this.favoriteRepository = favoriteRepository;
    this.pendingDocumentRepository = pendingDocumentRepository;
    this.userRepository = userRepository;
	}

	@ModelAttribute(value = "user")
	public User user(Model model, Principal principal, User user) {

		if (principal != null) {
			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}

		return user;
	}

	// show list category - table list
	@ModelAttribute("categories")
	public List<Category> showCategory(Model model) {
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);

		return categories;
	}

	@GetMapping(value = "/categories")
	public String categories(Model model, Principal principal) {
		Category category = new Category();
		model.addAttribute("category", category);

		return "admin/categories";
	}

	// add category
	@PostMapping(value = "/addCategory")
	public String addCategory(@Validated @ModelAttribute("category") Category category, ModelMap model,
	                          BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "failure");

			return "admin/categories";
		}

		categoryRepository.save(category);
		model.addAttribute("message", "successful!");

		return "redirect:/admin/categories";
	}

	// get Edit category
	@GetMapping(value = "/editCategory/{id}")
	public String editCategory(@PathVariable("id") Long id, ModelMap model) {
		Category category = categoryRepository.findById(id).orElse(null);

		model.addAttribute("category", category);

		return "admin/editCategory";
	}

	// delete category
	@GetMapping("/delete/{id}")
	public String delCategory(@PathVariable("id") Long id, Model model) {
		pendingDocumentRepository.deleteByDocument_CategoryId(id);
		favoriteRepository.deleteByDocument_CategoryId(id);
		documentRepository.deleteByFolder_CategoryId(id);
		folderRepository.deleteByCategory_CategoryId(id);
		categoryRepository.deleteById(id);

		model.addAttribute("message", "Delete successful!");

		return "redirect:/admin/categories";
	}
}
