package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ShopController extends CommomController {

	private final DocumentRepository documentRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	FavoriteRepository favoriteRepository;

	@Autowired
	CommomDataService commomDataService;

	public ShopController(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@GetMapping(value = "/documents")
	public String shop(Model model,
	                   Pageable pageable,
	                   @RequestParam("page") Optional<Integer> page,
	                   @RequestParam("size") Optional<Integer> size,
	                   User user) {

		int currentPage = page.orElse(1);
		int pageSize = size.orElse(12);

		Page<Document> productPage = findPaginated(PageRequest.of(currentPage - 1, pageSize));

		int totalPages = productPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		commomDataService.commonData(model, user);
		model.addAttribute("documents", productPage);

		return "web/shop";
	}

	public Page<Document> findPaginated(Pageable pageable) {

		List<Document> documentPage = documentRepository.findAllByDocumentStatus();

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

		return (Page<Document>) new PageImpl<Document>(list, PageRequest.of(currentPage, pageSize), documentPage.size());
	}

	// search document
	@GetMapping(value = "/searchDocument")
	public String showSearch(Model model,
	                         Pageable pageable,
	                         @RequestParam("keyword") String keyword,
	                         @RequestParam("size") Optional<Integer> size,
	                         @RequestParam("page") Optional<Integer> page,
	                         User user) {

		int currentPage = page.orElse(1);
		int pageSize = size.orElse(12);

		Page<Document> documentPage = findPaginatSearch(PageRequest.of(currentPage - 1, pageSize), keyword);

		int totalPages = documentPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		commomDataService.commonData(model, user);
		model.addAttribute("documents", documentPage);
		return "web/shop";
	}

	// search product
	public Page<Document> findPaginatSearch(Pageable pageable,
	                                        @RequestParam("keyword") String keyword) {

		List<Document> documentPage = documentRepository.searchDocument(keyword);

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

	// list books by category
	@GetMapping(value = "/documentByFolder")
	public String listDocumentId(Model model, @RequestParam("folderId") Long folderId, User user) {
		List<Document> documents = documentRepository.listDocumentByFolder(folderId);

		List<Document> listDocumentNew = new ArrayList<>();

		for (Document document : documents) {

			Document documentEntity = new Document();

			BeanUtils.copyProperties(document, documentEntity);

			Favorite save = favoriteRepository.selectSaves(documentEntity.getId(), user.getUserId());

			if (save != null) {
				documentEntity.favorite = true;
			} else {
				documentEntity.favorite = false;
			}
			listDocumentNew.add(documentEntity);

		}

		model.addAttribute("documents", listDocumentNew);
		commomDataService.commonData(model, user);
		return "web/shop";
	}

}
