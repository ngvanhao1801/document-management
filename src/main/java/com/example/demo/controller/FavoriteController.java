package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FavoriteController extends CommomController {

	private final DocumentRepository documentRepository;

	private final FavoriteRepository favoriteRepository;

	private final CommomDataService commomDataService;

	public FavoriteController(DocumentRepository documentRepository, FavoriteRepository favoriteRepository,
                            CommomDataService commomDataService) {
		this.documentRepository = documentRepository;
		this.favoriteRepository = favoriteRepository;
		this.commomDataService = commomDataService;
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
		if (document != null) {
			favorite.setDocument(document);
			favorite.setUser(user);

			favoriteRepository.save(favorite);

			document.setFavorites(document.getFavorites() + 1);
//      document.favorite = true;
			documentRepository.save(document);

		}
		commomDataService.commonData(model, user);

		return "redirect:/documents";
	}

	@GetMapping(value = "/doUnFavorite")
	public String doUnFavorite(Model model, User user, @RequestParam("id") Long id) {
		Favorite favorite = favoriteRepository.selectSaves(id, user.getUserId());
		Document document = documentRepository.findById(id).orElse(null);

		if (document != null && favorite != null) {
			favoriteRepository.delete(favorite);

			document.setFavorites(document.getFavorites() - 1);
//      document.favorite = false;
			documentRepository.save(document);

		}
		commomDataService.commonData(model, user);

		return "redirect:/documents";
	}
}
