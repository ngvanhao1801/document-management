package com.example.demo.controller.admin;

import com.example.demo.entity.Document;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@Controller
public class ReportController {

	private final UserRepository userRepository;

	private final DocumentRepository documentRepository;

	public ReportController(UserRepository userRepository,
	                        DocumentRepository documentRepository) {
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
	}

	// Statistics by product sold
	@GetMapping(value = "/admin/report-product")
	public String report(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<Document> documentList = documentRepository.findAllByDocumentStatus();
		model.addAttribute("documentList", documentList);

		return "admin/statistical-product";
	}

	// Statistics of products sold by year
	@RequestMapping(value = "/admin/report-year")
	public String reportYear(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<Object[]> listReportYearCommon = documentRepository.listReportYearCommon();
		model.addAttribute("listReportYearCommon", listReportYearCommon);

		return "admin/statistical-year";
	}

	// Statistics of products sold by month
	@RequestMapping(value = "/admin/report-month")
	public String reportMonth(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<Object[]> listReportMonthCommon = documentRepository.listReportMonthCommon();
		model.addAttribute("listReportMonthCommon", listReportMonthCommon);

		return "admin/statistical-month";
	}

	// Statistics of products sold by quarter
	@RequestMapping(value = "/admin/report-quarter")
	public String reportQuarter(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<Object[]> listReportQuarterCommon = documentRepository.listReportQuarterCommon();
		model.addAttribute("listReportQuarterCommon", listReportQuarterCommon);

		return "admin/statistical-quarter";
	}

	// Statistics by user
	@RequestMapping(value = "/admin/report-customer")
	public String reportOrderCustomer(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<Object[]> listReportCustomerCommon = documentRepository.listReportCustomerCommon();
		model.addAttribute("listReportCustomerCommon", listReportCustomerCommon);

		return "admin/statistical-customer";
	}

}
