package com.example.demo.controller.admin;

import com.example.demo.entity.Category;
import com.example.demo.entity.Folder;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.UserRepository;
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
public class FolderController {

	private final CategoryRepository categoryRepository;

	private final FolderRepository folderRepository;

	private final UserRepository userRepository;

	public FolderController(CategoryRepository categoryRepository, FolderRepository folderRepository,
	                        UserRepository userRepository) {
		this.categoryRepository = categoryRepository;
		this.folderRepository = folderRepository;
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

	@ModelAttribute("folders")
	public List<Folder> showFolder(Model model) {
		List<Folder> folders = folderRepository.findAll();
		model.addAttribute("folders", folders);

		return folders;
	}

	@GetMapping(value = "/folder")
	public String folders(Model model, Principal principal) {
		Folder folder = new Folder();
		model.addAttribute("folder", folder);

		return "admin/folder";
	}

	@PostMapping(value = "/addFolder")
	public String addFolder(@Validated @ModelAttribute("folder") Folder folder,
	                        ModelMap model,
	                        BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "failure");

			return "admin/folder";
		}

		folderRepository.save(folder);
		model.addAttribute("message", "successful!");

		return "redirect:/admin/folder";
	}

	@GetMapping(value = "/editFolder/{id}")
	public String editFolder(@PathVariable("id") Long id, ModelMap model) {
		Folder folder = folderRepository.findById(id).orElse(null);

		model.addAttribute("folder", folder);

		return "admin/editFolder";
	}

	@GetMapping("/deleteFolder/{id}")
	public String delFolder(@PathVariable("id") Long id, Model model) {
		folderRepository.deleteById(id);

		model.addAttribute("message", "Delete successful!");

		return "redirect:/admin/folder";
	}

	@ModelAttribute("categoryList")
	public List<Category> showCategory(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		return categoryList;
	}

}
