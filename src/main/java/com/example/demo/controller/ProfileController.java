package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ProfileController extends CommomController {

	private final UserRepository userRepository;

	private final DocumentRepository documentRepository;

	private final CommomDataService commomDataService;

	public ProfileController(UserRepository userRepository,
	                         DocumentRepository documentRepository,
	                         CommomDataService commomDataService) {
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.commomDataService = commomDataService;
	}

	@GetMapping(value = "/profile")
	public String profile(Model model,
	                      Principal principal,
	                      User user,
	                      @RequestParam("page") Optional<Integer> page,
	                      @RequestParam("size") Optional<Integer> size) {

		if (principal != null) {

			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}

		int totalDocumentUpload = documentRepository.countDocumentUploadByUser(user.getUserId());
		model.addAttribute("totalDocumentUpload", totalDocumentUpload);

		int currentPage = page.orElse(1);
		int pageSize = size.orElse(10);

		Page<Document> documentPage = findPaginated(PageRequest.of(currentPage - 1, pageSize), user);

		int totalPages = documentPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		commomDataService.commonData(model, user);
		model.addAttribute("documentByUser", documentPage);

		return "web/profile";
	}

	public Page<Document> findPaginated(Pageable pageable, User user) {

		List<Document> documentPage = documentRepository.getListDocumentUpload(user.getUserId());

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Document> list;

		if (documentPage.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, documentPage.size());
			list = documentPage.subList(startItem, toIndex);
		}

		return new PageImpl<Document>(list, PageRequest.of(currentPage, pageSize), documentPage.size());
	}

}
