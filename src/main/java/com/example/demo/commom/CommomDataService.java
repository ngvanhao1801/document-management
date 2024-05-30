package com.example.demo.commom;

import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class CommomDataService {

	private final FavoriteRepository favoriteRepository;

	private final DocumentRepository documentRepository;

	public CommomDataService(DocumentRepository documentRepository,
	                         FavoriteRepository favoriteRepository) {
		this.documentRepository = documentRepository;
		this.favoriteRepository = favoriteRepository;
	}

	public void commonData(Model model, User user) {
		listFolderByDocumentName(model);
		Integer totalSave = 0;
		if (user != null) {
			totalSave = favoriteRepository.selectCountSave(user.getUserId());
		}

		model.addAttribute("totalSave", totalSave);

	}

	//count Document By Folder
	public void listFolderByDocumentName(Model model) {

		List<Object[]> countDocumentByFolder = documentRepository.listFolderByDocumentName();
		model.addAttribute("countDocumentByFolder", countDocumentByFolder);
	}

}
