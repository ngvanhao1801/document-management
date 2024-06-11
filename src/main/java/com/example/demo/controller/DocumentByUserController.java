package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.dto.MailInfo;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.SendMailService;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class DocumentByUserController {

	private final DocumentRepository documentRepository;

	private final PendingDocumentRepository pendingDocumentRepository;

	private final FolderRepository folderRepository;

	private final UserRepository userRepository;

	private final DocumentStatusRepository documentStatusRepository;

	private final CommomDataService commomDataService;

	private final SendMailService sendMailService;

	@Value("${upload.path}")
	private String pathUploadImage;

	@Value("${upload.file.path}")
	private String pathUploadFile;

	public DocumentByUserController(DocumentRepository documentRepository,
	                                PendingDocumentRepository pendingDocumentRepository,
	                                FolderRepository folderRepository,
	                                UserRepository userRepository,
	                                DocumentStatusRepository documentStatusRepository,
	                                CommomDataService commomDataService,
	                                SendMailService sendMailService) {
		this.documentRepository = documentRepository;
		this.pendingDocumentRepository = pendingDocumentRepository;
		this.folderRepository = folderRepository;
		this.userRepository = userRepository;
		this.documentStatusRepository = documentStatusRepository;
		this.commomDataService = commomDataService;
		this.sendMailService = sendMailService;
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

		commomDataService.commonData(model, user);

		List<Document> documents = documentRepository.getListDocumentUpload(user.getUserId());
		model.addAttribute("documents", documents);
		model.addAttribute("document", new Document());

		return "web/add_document_by_user";
	}

	@PostMapping(value = "/uploadDocument")
	public String addDocument(@ModelAttribute("document") Document document,
	                          ModelMap model,
	                          @RequestParam("file") MultipartFile[] files,
	                          HttpServletRequest httpServletRequest,
	                          BindingResult result, User user) {

		if (result.hasErrors()) {
			return "web/add_document_by_user";
		}

		for (MultipartFile file : files) {
			try {
				String nameFile = file.getOriginalFilename();
				String filePath = null;
				switch (Objects.requireNonNull(file.getContentType())) {
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
		Long statusId = 1L; // Trạng thái "Chờ xét duyệt"
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

		String adminEmail = "ngohao181102@gmail.com";
		String emailSubject = "Yêu cầu xác nhận đăng tải tài liệu từ người dùng " + user.getName();
		String emailBody = "<div>"
				+ "<p>Người dùng " + user.getName() + " vừa đăng tải tài liệu mới có tên là: " + document.getDocumentName() + "</p>"
				+ "<p>Email người dùng: " + user.getEmail() + "</p>"
				+ "</div>";

		MailInfo mailInfo = new MailInfo(adminEmail, emailSubject, emailBody);
		sendMailService.queue(mailInfo);

		return "redirect:/list-documents";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}

	@ModelAttribute("folderList")
	public List<Folder> showFolder(Model model) {
		List<Folder> folderList = folderRepository.findAll();
		model.addAttribute("folderList", folderList);

		return folderList;
	}

	@ModelAttribute("userList")
	public List<User> showUser(Model model) {
		List<User> userList = userRepository.findAllByRoles();
		model.addAttribute("userList", userList);

		return userList;
	}

	@GetMapping(value = "/edit-document-upload/{id}")
	public String editDocument(@PathVariable("id") Long id, ModelMap model) {
		Document document = documentRepository.findById(id).orElse(null);

		if (document == null) {
			return "web/notFound";
		}

		pendingDocumentRepository.deleteByDocumentId(id);

		model.addAttribute("editDocument", document);
		model.addAttribute("folderList", folderRepository.findAll());
		model.addAttribute("userList", userRepository.findAllByRoles());

		return "web/edit_document_upload";
	}

}
