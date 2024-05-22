package com.example.demo.controller;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentStatus;
import com.example.demo.entity.PendingDocument;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.DocumentStatusRepository;
import com.example.demo.repository.PendingDocumentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class DocumentByUserController {

	private final DocumentRepository documentRepository;

	private final PendingDocumentRepository pendingDocumentRepository;

	private final UserRepository userRepository;

	private final DocumentStatusRepository documentStatusRepository;

	private final HttpSession httpSession;

	@Value("${upload.path}")
	private String pathUploadImage;

	@Value("${upload.file.path}")
	private String pathUploadFile;

	public DocumentByUserController(DocumentRepository documentRepository,
	                                PendingDocumentRepository pendingDocumentRepository, UserRepository userRepository,
	                                DocumentStatusRepository documentStatusRepository, HttpSession httpSession) {
		this.documentRepository = documentRepository;
		this.pendingDocumentRepository = pendingDocumentRepository;
		this.userRepository = userRepository;
		this.documentStatusRepository = documentStatusRepository;
		this.httpSession = httpSession;
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

	@GetMapping(value = "/list-documents")
	public String documents(Model model, User user) {

		int totalDocumentUpload = documentRepository.countDocumentUploadByUser(user.getUserId());

		List<Document> documents = documentRepository.getListDocumentUpload(user.getUserId());
		model.addAttribute("documents", documents);
		model.addAttribute("totalDocumentUpload", totalDocumentUpload);
		model.addAttribute("document", new Document());

		return "web/add_document_by_user";
	}

	@PostMapping(value = "/uploadDocument")
	public String addDocument(@ModelAttribute("document") Document document,
	                          ModelMap model,
	                          @RequestParam("file") MultipartFile[] files,
	                          HttpServletRequest httpServletRequest,
	                          BindingResult result) {

		if (result.hasErrors()) {
			return "web/add_document_by_user";
		}

		for (MultipartFile file : files) {
			try {
				String nameFile = file.getOriginalFilename();
				String filePath = null;
				switch (file.getContentType()) {
					case "image/jpeg":
					case "image/png":
					case "image/gif":
					case "image/bmp":
						filePath = pathUploadImage + "/" + nameFile;
						break;
					case "application/pdf":
					case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
					case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
					case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
						filePath = pathUploadFile + "/" + nameFile;
						break;
					default:
						// Xử lý loại tệp không được hỗ trợ ở đây
						break;
				}

				if (filePath != null) {
					File convFile = new File(filePath);
					FileOutputStream fos = new FileOutputStream(convFile);
					fos.write(file.getBytes());
					fos.close();
				}

				// Lưu thông tin loại phương tiện vào cơ sở dữ liệu
				document.setMediaType(file.getContentType());

				// Lưu tên file vào đối tượng Document
				if (file.getContentType().startsWith("image")) {
					document.setDocumentImage(nameFile);
				}
				if (file.getContentType().equals("application/pdf")
						|| file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
						|| file.getContentType().equals("application/vnd.openxmlformats-officedocument.presentationml" +
						".presentation")
						|| file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
					document.setDocumentFile(nameFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Long statusId = 2L; // Trạng thái "Chờ xét duyệt"
		Optional<DocumentStatus> documentStatusOptional = documentStatusRepository.findById(statusId);
		if (documentStatusOptional.isEmpty()) {
			throw new RuntimeException("Document status not found");
		}
		DocumentStatus documentStatus = documentStatusOptional.get();

		// Thiết lập trạng thái cho Document
		document.setDocumentStatus(documentStatus);

		// Lưu Document vào cơ sở dữ liệu
		Document savedDocument = documentRepository.save(document);

		// Tạo và thiết lập trạng thái cho PendingDocument
		PendingDocument pendingDocument = new PendingDocument();
		pendingDocument.setDocument(savedDocument);
		pendingDocument.setUser(savedDocument.getUser());
		pendingDocument.setUploadDate(new Date());
		pendingDocument.setDocumentStatus(documentStatus);

		// Lưu PendingDocument vào cơ sở dữ liệu
		pendingDocumentRepository.save(pendingDocument);

		if (savedDocument != null) {
			model.addAttribute("message", "Update success");
			model.addAttribute("document", document);
		} else {
			model.addAttribute("message", "Update failure");
			model.addAttribute("document", document);
		}
		return "redirect:/list-documents";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}

	@ModelAttribute("statusList")
	public List<DocumentStatus> showStatus(Model model) {
		List<DocumentStatus> statusList = documentStatusRepository.findAll();
		httpSession.setAttribute("statusList", statusList);
		model.addAttribute("statusList", statusList);

		return statusList;
	}

	@GetMapping(value = "/editDocumentUpload/{id}")
	public String editDocument(@PathVariable("id") Long id, ModelMap model) {
		Document document = documentRepository.findById(id).orElse(null);

		model.addAttribute("document", document);

		return "web/edit_document_upload";
	}

}
