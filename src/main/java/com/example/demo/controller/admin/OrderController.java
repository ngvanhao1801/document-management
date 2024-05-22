package com.example.demo.controller.admin;

import com.example.demo.dto.OrderExcelExporter;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class OrderController {

	@Autowired
	OrderDetailService orderDetailService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PendingDocumentRepository pendingDocumentRepository;
	
	@Autowired
	DocumentStatusRepository documentStatusRepository;

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

		List<PendingDocument> pendingDocuments = pendingDocumentRepository.findAll();
		model.addAttribute("pendingDocuments", pendingDocuments);

		return "admin/orders";
	}

	@GetMapping("/order/detail/{id}")
	public ModelAndView detail(ModelMap model, @PathVariable("id") Long id) {

		List<OrderDetail> listO = orderDetailRepository.findByOrderId(id);

		model.addAttribute("amount", orderRepository.findById(id).get().getAmount());
		model.addAttribute("orderDetail", listO);
		model.addAttribute("orderId", id);
		// set active front-end
		model.addAttribute("menuO", "menu");
		return new ModelAndView("admin/editOrder", model);
	}

	@RequestMapping("/order/cancel/{id}")
	@Transactional
	public ModelAndView cancel(ModelAndView model, @PathVariable("id") Long id) {
//		Optional<Order> optionalOrder = orderRepository.findById(id);
//		if (optionalOrder == null) {
//			return new ModelAndView("forward:/admin/orders");
//		}
//		Order oReal = optionalOrder.get();
//		oReal.setStatus((short) 3);
//		orderRepository.save(oReal);
//
//		orderDetailRepository.deleteByOrder(oReal);
//
//		return new ModelAndView("forward:/admin/orders");
		Optional<PendingDocument> pendingDocumentOptional = pendingDocumentRepository.findById(id);
		if (pendingDocumentOptional.isEmpty()) {
			return new ModelAndView("forward:/admin/orders");
		}

		Long statusId = 3L; // Trạng thái "Hủy xét duyệt"
		Optional<DocumentStatus> documentStatusOptional = documentStatusRepository.findById(statusId);
		if (documentStatusOptional.isEmpty()) {
			throw new RuntimeException("Document status not found");
		}
		DocumentStatus documentStatus = documentStatusOptional.get();

		PendingDocument pendingDocument = pendingDocumentOptional.get();
		pendingDocument.setDocumentStatus(documentStatus);

		pendingDocumentRepository.save(pendingDocument);

		return new ModelAndView("forward:/admin/orders");

	}

	@RequestMapping("/order/confirm/{id}")
	public ModelAndView confirm(ModelMap model, @PathVariable("id") Long id) {
		Optional<PendingDocument> pendingDocumentOptional = pendingDocumentRepository.findById(id);
		if (pendingDocumentOptional.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}

		Long statusId = 2L; // Trạng thái "Chờ xét duyệt"
		Optional<DocumentStatus> documentStatusOptional = documentStatusRepository.findById(statusId);
		if (documentStatusOptional.isEmpty()) {
			throw new RuntimeException("Document status not found");
		}
		DocumentStatus documentStatus = documentStatusOptional.get();

		PendingDocument pendingDocument = pendingDocumentOptional.get();
		pendingDocument.setDocumentStatus(documentStatus);

		pendingDocumentRepository.save(pendingDocument);

		return new ModelAndView("forward:/admin/orders", model);
	}

	@RequestMapping("/order/delivered/{id}")
	public ModelAndView delivered(ModelMap model, @PathVariable("id") Long id) {
//		Optional<Order> optionalOrder = orderRepository.findById(id);
//		if (optionalOrder == null) {
//			return new ModelAndView("forward:/admin/orders", model);
//		}
//		Order oReal = optionalOrder.get();
//		oReal.setStatus((short) 2);
//		orderRepository.save(oReal);
//
//		Product p = null;
//		List<OrderDetail> listDe = orderDetailRepository.findByOrderId(id);
//		for (OrderDetail od : listDe) {
//			p = od.getProduct();
//			p.setQuantity(p.getQuantity() - od.getQuantity());
//			productRepository.save(p);

		Optional<PendingDocument> pendingDocumentOptional = pendingDocumentRepository.findById(id);
		if (pendingDocumentOptional.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}

		Long statusId = 4L; // Trạng thái "Đã xét duyệt"
		Optional<DocumentStatus> documentStatusOptional = documentStatusRepository.findById(statusId);
		if (documentStatusOptional.isEmpty()) {
			throw new RuntimeException("Document status not found");
		}
		DocumentStatus documentStatus = documentStatusOptional.get();

		PendingDocument pendingDocument = pendingDocumentOptional.get();
		pendingDocument.setDocumentStatus(documentStatus);

		pendingDocumentRepository.save(pendingDocument);

		return new ModelAndView("forward:/admin/orders", model);
		}

	}

	// to excel
//	@GetMapping(value = "/export")
//	public void exportToExcel(HttpServletResponse response) throws IOException {
//
//		response.setContentType("application/octet-stream");
//		String headerKey = "Content-Disposition";
//		String headerValue = "attachement; filename=orders.xlsx";
//
//		response.setHeader(headerKey, headerValue);
//
//		List<Order> lisOrders = orderDetailService.listAll();
//
//		OrderExcelExporter excelExporter = new OrderExcelExporter(lisOrders);
//		excelExporter.export(response);
//
//	}
