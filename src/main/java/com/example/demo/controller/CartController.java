package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Folder;
import com.example.demo.entity.User;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {

	private final CommomDataService commomDataService;

	private final UserRepository userRepository;

	private final FolderRepository folderRepository;

	public CartController(CommomDataService commomDataService,
	                      UserRepository userRepository,
	                      FolderRepository folderRepository) {
		this.commomDataService = commomDataService;
		this.userRepository = userRepository;
		this.folderRepository = folderRepository;
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

	@GetMapping(value = "/checkout")
	public String checkOut(Model model, User user) {

		commomDataService.commonData(model, user);

		return "/web/shoppingCart-checkout";
	}

	@ModelAttribute("folderList")
	public List<Folder> showFolderList(Model model) {
		List<Folder> folderList = folderRepository.findAll();
		model.addAttribute("folderList", folderList);

		return folderList;
	}

}
