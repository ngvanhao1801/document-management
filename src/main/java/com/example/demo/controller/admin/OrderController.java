package com.example.demo.controller.admin;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentStatus;
import com.example.demo.entity.PendingDocument;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.DocumentStatusRepository;
import com.example.demo.repository.PendingDocumentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SendMailService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class OrderController {

	private final UserRepository userRepository;

	private final PendingDocumentRepository pendingDocumentRepository;

	private final DocumentStatusRepository documentStatusRepository;

	private final DocumentRepository documentRepository;

	private final SendMailService sendMailService;

	public OrderController(UserRepository userRepository,
                         PendingDocumentRepository pendingDocumentRepository,
                         DocumentStatusRepository documentStatusRepository,
                         DocumentRepository documentRepository, SendMailService sendMailService) {
		this.userRepository = userRepository;
		this.pendingDocumentRepository = pendingDocumentRepository;
		this.documentStatusRepository = documentStatusRepository;
		this.documentRepository = documentRepository;
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

	// list order
	@GetMapping(value = "/orders")
	public String documentPending(Model model, Principal principal) {

		List<PendingDocument> pendingDocuments = pendingDocumentRepository.findAllByDocumentStatus();
		model.addAttribute("pendingDocuments", pendingDocuments);

		return "admin/orders";
	}

	@RequestMapping("/order/cancel/{id}")
	@Transactional
	public ModelAndView cancel(ModelAndView model,
	                           @PathVariable("id") Long id) {
		Optional<PendingDocument> pendingDocumentOptional = pendingDocumentRepository.findById(id);
		if (pendingDocumentOptional.isEmpty()) {
			return new ModelAndView("forward:/admin/orders");
		}

		PendingDocument pendingDocument = pendingDocumentOptional.get();

		// Lấy thông tin về Document từ PendingDocument
		Document document = pendingDocument.getDocument();

		// Cập nhật trường statusIs trong Document
		Long statusId = 2L; // Trạng thái "Hủy xét duyệt"
		Optional<DocumentStatus> documentStatusOptional = documentStatusRepository.findById(statusId);
		if (documentStatusOptional.isEmpty()) {
			throw new RuntimeException("Document status not found");
		}
		DocumentStatus documentStatus = documentStatusOptional.get();
		document.setDocumentStatus(documentStatus);

		// Lưu Document đã được cập nhật
		documentRepository.save(document);

		// Cập nhật trạng thái trong PendingDocument
		pendingDocument.setDocumentStatus(documentStatus);
		pendingDocumentRepository.save(pendingDocument);

		String body = "<div>"
				+ "<h3>Tài liệu của bạn vừa đăng tải đã bị hủy.</h3>"
				+ "<div>"
				+ "<p>Tên tài liệu: " + document.getDocumentName() + "</p>"
				+ "<p>Ngày đăng tải: " + document.getUploadDate() + "</p>"
				+ "</div>";
		sendMailService.queue(document.getUser().getEmail(), "Phản hồi về đăng tải tài liệu", body);

		return new ModelAndView("forward:/admin/orders");
	}

	@RequestMapping("/order/confirm/{id}")
	public ModelAndView confirm(ModelMap model,
	                            @PathVariable("id") Long id) {
		Optional<PendingDocument> pendingDocumentOptional = pendingDocumentRepository.findById(id);
		if (pendingDocumentOptional.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}

		PendingDocument pendingDocument = pendingDocumentOptional.get();

		// Lấy thông tin về Document từ PendingDocument
		Document document = pendingDocument.getDocument();

		Long statusId = 3L; // Trạng thái "Đã xét duyệt"
		Optional<DocumentStatus> documentStatusOptional = documentStatusRepository.findById(statusId);
		if (documentStatusOptional.isEmpty()) {
			throw new RuntimeException("Document status not found");
		}
		DocumentStatus documentStatus = documentStatusOptional.get();
		document.setDocumentStatus(documentStatus);

		documentRepository.save(document);

		pendingDocument.setDocumentStatus(documentStatus);
		pendingDocumentRepository.save(pendingDocument);

		String body = "<div>"
				+ "<h3>Tài liệu của bạn vừa đăng tải đã được xét duyệt thành công.</h3>"
				+ "<div>"
				+ "<p>Tên tài liệu: " + document.getDocumentName() + "</p>"
				+ "<p>Ngày đăng tải: " + document.getUploadDate() + "</p>"
				+ "</div>";
		sendMailService.queue(document.getUser().getEmail(), "Phản hồi về đăng tải tài liệu", body);

		return new ModelAndView("forward:/admin/orders", model);
	}

}