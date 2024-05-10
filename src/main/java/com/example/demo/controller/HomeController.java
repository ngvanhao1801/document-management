package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController extends CommomController {
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CommomDataService commomDataService;
	
	@Autowired
	FavoriteRepository favoriteRepository;

	private final DocumentRepository documentRepositoryl;

	public HomeController(DocumentRepository documentRepositoryl) {
		this.documentRepositoryl = documentRepositoryl;
	}

	@GetMapping(value = "/")
	public String home(Model model, User user) {

		commomDataService.commonData(model, user);
		bestSaleProduct20(model, user);
		return "web/home";
	}
	
	// list product ở trang chủ limit 10 sản phẩm mới nhất
	@ModelAttribute("listDocument10")
	public List<Document> listDocument10(Model model) {
		List<Document> documentList = documentRepositoryl.listDocumentNew20();
		model.addAttribute("c", documentList);
		return documentList;
	}
	
	// Top 20 best sale.
	public void bestSaleProduct20(Model model, User customer) {
		List<Object[]> productList = productRepository.bestSaleProduct20();
		if (productList != null) {
			ArrayList<Integer> listIdProductArrayList = new ArrayList<>();
			for (int i = 0; i < productList.size(); i++) {
				String id = String.valueOf(productList.get(i)[0]);
				listIdProductArrayList.add(Integer.valueOf(id));
			}
			List<Product> listProducts = productRepository.findByInventoryIds(listIdProductArrayList);

			List<Product> listProductNew = new ArrayList<>();

			for (Product product : listProducts) {

				Product productEntity = new Product();

				BeanUtils.copyProperties(product, productEntity);

				Favorite save = favoriteRepository.selectSaves(productEntity.getProductId(), customer.getUserId());

				if (save != null) {
					productEntity.favorite = true;
				} else {
					productEntity.favorite = false;
				}
				listProductNew.add(productEntity);

			}

			model.addAttribute("bestSaleProduct20", listProductNew);
		}
	}

	@GetMapping(value = "/lien-he")
	public String contact(Model model, User user) {

		commomDataService.commonData(model, user);
		return "web/contact";
	}
}
