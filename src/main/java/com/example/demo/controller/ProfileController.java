package com.example.demo.controller;

import com.example.demo.commom.CommomDataService;
import com.example.demo.entity.Document;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ProfileController extends CommomController{

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	CommomDataService commomDataService;

	@GetMapping(value = "/profile")
	public String profile(Model model, Principal principal, User user, Pageable pageable,
						  @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		if (principal != null) {

			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}
		
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(10);

		Page<Document> documentPage = findPaginated(PageRequest.of(currentPage - 1, pageSize), user);

		int totalPages = documentPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		commomDataService.commonData(model, user);
		model.addAttribute("documentByUser", documentPage);

		return "web/profile";
	}

	public Page<Document> findPaginated(Pageable pageable, User user) {

		List<Document> documentPage = documentRepository.getListDocumentUpload(user.getUserId());

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Document> list;

		if (documentPage.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, documentPage.size());
			list = documentPage.subList(startItem, toIndex);
		}

    return new PageImpl<Document>(list, PageRequest.of(currentPage, pageSize), documentPage.size());
	}
	
	@GetMapping("/order/detail/{order_id}")
	public ModelAndView detail(Model model, Principal principal, User user, @PathVariable("order_id") Long id) {

		if (principal != null) {

			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}
		
		List<OrderDetail> listO = orderDetailRepository.findByOrderId(id);

//		model.addAttribute("amount", orderRepository.findById(id).get().getAmount());
		model.addAttribute("orderDetail", listO);
//		model.addAttribute("orderId", id);
		// set active front-end
//		model.addAttribute("menuO", "menu");
		commomDataService.commonData(model, user);
		
		return new ModelAndView("web/historyOrderDetail");
	}
	
	@RequestMapping("/order/cancel/{order_id}")
	@Transactional
	public ModelAndView cancel(ModelAndView model, @PathVariable("order_id") Long id) {
		Optional<Order> optionalOrder = orderRepository.findById(id);
		if (optionalOrder == null) {
			return new ModelAndView("redirect:/profile");
		}
		Order oReal = optionalOrder.get();
		oReal.setStatus((short) 3);
		orderRepository.save(oReal);

		orderDetailRepository.deleteByOrder(oReal);

		return new ModelAndView("redirect:/profile");
	}

}
