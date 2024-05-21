package com.example.demo.controller;

import com.example.demo.entity.Feedback;
import com.example.demo.repository.FeedbackRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FeedbackController {

	private final FeedbackRepository feedbackRepository;

	public FeedbackController(FeedbackRepository feedbackRepository) {
		this.feedbackRepository = feedbackRepository;
	}

	@GetMapping(value = "/feedbacks")
	public List<?> getListComment(@RequestParam("id") Long id,
	                              Model model) {

		List<Feedback> listComment = feedbackRepository.listCommentByDocument(id);
		model.addAttribute("comments", listComment);

		return listComment;
	}

	@PostMapping(value = "/addFeedback")
	public String addFeedback(@Validated @ModelAttribute("feedback") Feedback feedback,
	                          ModelMap model,
	                          BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "failure");

			return "web/shop";
		}

		feedbackRepository.save(feedback);
		model.addAttribute("message", "successful!");

		return "redirect:/feedbacks";

	}

}
