package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductDetailController extends CommomController{
	
	@Autowired
	ProductRepository productRepository;

	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	CommomDataService commomDataService;

	@GetMapping(value = "documentDetail")
	public String documentDetail(@RequestParam("id") Long id, Model model, User user) {

		Document document = documentRepository.findById(id).orElse(null);
		model.addAttribute("document", document);

		commomDataService.commonData(model, user);
		listDocumentByFolder10(model, document.getFolder().getFolderId());

		return "web/productDetail";
	}
	
	// Gợi ý top 10 sản phẩm cùng loại
	public void listDocumentByFolder10(Model model, Long folderId) {
		List<Document> documents = documentRepository.listDocumentByFolder10(folderId);
		model.addAttribute("countDocumentByFolder", documents);
	}
}
