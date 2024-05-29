package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class HomeController extends CommomController {

  private final CommomDataService commomDataService;

  private final FavoriteRepository favoriteRepository;

  private final DocumentRepository documentRepository;

  public HomeController(ProductRepository productRepository,
                        CommomDataService commomDataService,
                        FavoriteRepository favoriteRepository,
                        DocumentRepository documentRepository) {
    this.productRepository = productRepository;
    this.commomDataService = commomDataService;
    this.favoriteRepository = favoriteRepository;
    this.documentRepository = documentRepository;
  }

  @GetMapping(value = "/")
  public String home(Model model, @ModelAttribute("user") User user) {

    commomDataService.commonData(model, user);
    return "web/home";
  }

  @ModelAttribute("listDocument10")
  public List<Document> listDocumentNew(Model model, @ModelAttribute("user") User user) {
    List<Document> documentList = documentRepository.listDocumentNew();

    for (Document document : documentList) {
      Favorite favorite = favoriteRepository.selectSaves(document.getId(), user.getUserId());
      if (favorite != null) {
        document.setFavorite(true);
      } else {
        document.setFavorite(false);
      }
    }

    model.addAttribute("documentNew", documentList);
    commomDataService.commonData(model, user);
    return documentList;
  }

  @GetMapping(value = "/lien-he")
  public String contact(Model model, @ModelAttribute("user") User user) {

    commomDataService.commonData(model, user);
    return "web/contact";
  }
}
