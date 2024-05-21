package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FavoriteController extends CommomController {

	private final DocumentRepository documentRepository;

	@Autowired
	FavoriteRepository favoriteRepository;

	@Autowired
	CommomDataService commomDataService;

	public FavoriteController(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@GetMapping(value = "/favorite")
	public String favorite(Model model, User user) {
		List<Favorite> favorites = favoriteRepository.selectAllSaves(user.getUserId());
		commomDataService.commonData(model, user);
		model.addAttribute("favorites", favorites);
		return "web/favorite";
	}

	@GetMapping(value = "/doFavorite")
	public String doFavorite(Model model, Favorite favorite, User user, @RequestParam("id") Long id) {
		Document document = documentRepository.findById(id).orElse(null);
		favorite.setDocument(document);
		favorite.setUser(user);
		assert document != null;
		document.setFavorite(true);
		favoriteRepository.save(favorite);
		commomDataService.commonData(model, user);
		return "redirect:/documents";
	}

	@GetMapping(value = "/doUnFavorite")
	public String doUnFavorite(Model model, Document document, User user, @RequestParam("id") Long id) {
		Favorite favorite = favoriteRepository.selectSaves(id, user.getUserId());
		document = documentRepository.findById(id).orElse(null);
		assert document != null;
		document.setFavorite(false);
		favoriteRepository.delete(favorite);
		commomDataService.commonData(model, user);
		return "redirect:/documents";
	}
}
